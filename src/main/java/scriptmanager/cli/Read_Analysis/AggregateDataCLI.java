package scriptmanager.cli.Read_Analysis;

import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import scriptmanager.objects.ToolDescriptions;
import scriptmanager.scripts.Read_Analysis.AggregateData;
import scriptmanager.util.ExtensionFileFilter;

/**
	Read_AnalysisCLI/AggregateDataCLI
*/
@Command(name = "aggregate-data", mixinStandardHelpOptions = true,
	description = ToolDescriptions.aggregate_data_description,
	version = "ScriptManager "+ ToolDescriptions.VERSION,
	sortOptions = false,
	exitCodeOnInvalidInput = 1,
	exitCodeOnExecutionException = 1)
public class AggregateDataCLI implements Callable<Integer> {
	
	@Parameters( index="0..", description = "The matrix files whose statistics we want.")
	private File[] matrixFiles;

	@Option(names = {"-f", "--files"}, description = "Input file list of matrix filepaths to aggregate (formatted so each path is on its own line)")
	private boolean fileList = false;
	@Option(names = {"-o", "--output"}, description = "Specify output file (default = <input1>_SCORES.out, <input2_SCORES.out, ... or ALL_SCORES.out if -m flag is used)")
	private File output;
	@Option(names = {"-z", "--gzip"}, description = "output compressed output (default=false)")
	private boolean zip = false;
	
	@ArgGroup(exclusive = true, heading = "Aggregation Method%n")
	AggType aggr = new AggType();
	static class AggType{
		@Option(names = {"--sum"}, description = "use summation method(default)")
		private boolean sum = false;
		@Option(names = {"--avg"}, description = "use average method")
		private boolean avg = false;
		@Option(names = {"--med"}, description = "use median method")
		private boolean med = false;
		@Option(names = {"--mod"}, description = "use mode method")
		private boolean mod = false;
		@Option(names = {"--min"}, description = "use minimum method")
		private boolean min = false;
		@Option(names = {"--max"}, description = "use maximum method")
		private boolean max = false;
		@Option(names = {"--var"}, description = "use positional variance method")
		private boolean var = false;
	}
	
	@Option(names = {"-m", "--merge"}, description = "merge to one file")
	private boolean merge = false;
	@Option(names = {"-r", "--start-row"}, description = "")
	private int startROW = 1;
	@Option(names = {"-l", "--start-col"}, description = "")
	private int startCOL = 2;
	
	private int aggType = 0;
	private ArrayList<File> matFiles = new ArrayList<File>();
	
	@Override
	public Integer call() throws Exception {
		System.err.println( ">AggregateDataCLI.call()" );
		String validate = validateInput();
		if(!validate.equals("")){
			System.err.println( validate );
			System.err.println("Invalid input. Check usage using '-h' or '--help'");
			System.exit(1);
		}
		
		AggregateData script_obj = new AggregateData(matFiles, output, merge, startROW, startCOL, aggType, zip);
		script_obj.run();
		
		System.err.println(script_obj.getMessage());
		return(0);
	}
	
	private String validateInput() throws IOException {
		String r = "";
		
		if(matrixFiles==null){
			r += "(!)Please indicate at least one file.\n";
			return(r);
		//Import files as Vector list (scan input file if -f flag used)
		}else if(fileList){		//load files from input filelist
			if(matrixFiles.length>1){
				r += "(!)Please indicate only one file with matrix filepaths when using the -f flag.\n";
				return(r);
			}else if(!matrixFiles[0].exists()){
				r += "(!)File of list of file inputs does not exist: " + matrixFiles[0].getCanonicalPath() + "\n";
				return(r);
			}else{
				Scanner scan = new Scanner(matrixFiles[0]);
				while (scan.hasNextLine()) {
					matFiles.add(new File(scan.nextLine().trim()));
				}
				scan.close();
			}
		}else{		//load input files into bam vector
			for(int x=0; x<matrixFiles.length; x++){
				matFiles.add(matrixFiles[x]);
			}
		}
		//check each file in Vector
		for(int x=0; x<matFiles.size(); x++){
			File MAT = matFiles.get(x);
			//no ext check
			//check input exists
			if(!MAT.exists()|| MAT.isDirectory()){
				r += "(!)matrix[" + x + "] file does not exist: " + MAT.getName() + "\n";
			}
		}
		if(!r.equals("")){ return(r); }
		//check output filename is valid
		if(output!=null){
			//no check ext
			//check directory
			if( output.isDirectory() ){
				if( !output.exists() ){
					r += "(!)Directory must exist. Check your -o directory path.";
				}
			//check file
			} else {
				//validate file if merge, copy error message otherwise
				if( merge ){
					if( output.getParent()==null ){
						//Adds .gz extension if needed
						if (zip && !ExtensionFileFilter.getExtension(output).equals("gz")){
							output = new File(output.getAbsolutePath() + ".gz");
						}
// 						System.err.println("output file to current working directory");
					} else if(!new File(output.getParent()).exists()){
						r += "(!)Check directory of output file exists: " + output.getParent() + "\n";
					}
				} else if(matFiles.size()!=1){
					r += "(!)Must indicate directory (not a file) if you're not using the merge option.";
				}
			}
		}else{
			output = new File(System.getProperty("user.dir"));
		}
		
		//Set numeric indicator for aggregation method
		if(aggr.avg) { aggType = 1; }
		else if(aggr.med) { aggType = 2; }
		else if(aggr.mod) { aggType = 3; }
		else if(aggr.min) { aggType = 4; }
		else if(aggr.max) { aggType = 5; }
		else if(aggr.var) { aggType = 6; }
		
		//validate row&column start indexes
		if(startROW<0){ r += "(!)Row start must not be less than zero\n"; }
		if(startCOL<0){ r += "(!)Column start must not be less than zero\n"; }
				
		return(r);
	}
}