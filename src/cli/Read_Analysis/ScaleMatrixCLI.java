package cli.Read_Analysis;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import java.io.File;
import java.io.IOException;

import util.ExtensionFileFilter;
import scripts.Read_Analysis.ScaleMatrix;

/**
	Read_AnalysisCLI/ScaleMatrixCLI
*/
@Command(name = "scale-matrix", mixinStandardHelpOptions = true,
		description = "Apply a user-specified scaling factor to tab-delimited matrix data",
		sortOptions = false)
public class ScaleMatrixCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "Input matrix file")
	private File matrixFile;
	
	@Option(names = {"-o", "--output"}, description = "Specify output file (default = <matrixFilename>_SCALE.tab)")
	private File output = null;
	@Option(names = {"-s", "--scaling-factor"}, description = "scaling factor (default=1)")
	private double scale = 1;
	@Option(names = {"-r", "--start-row"}, description = "")
	private int startROW = 1;
	@Option(names = {"-l", "--start-col"}, description = "")
	private int startCOL = 2;
	
	
	@Override
	public Integer call() throws Exception {
		System.err.println( ">ScaleMatrixCLI.call()" );
		String validate = validateInput();
		if(!validate.equals("")){
			System.err.println( validate );
			System.err.println("Invalid input. Check usage using '-h' or '--help'");
			return(1);
		}		
		
		ScaleMatrix script_obj = new ScaleMatrix(matrixFile, output, scale, startROW, startCOL);
		script_obj.run();
		
		System.err.println("All Matrices Scaled.");
		return(0);
	}
	
	private String validateInput() throws IOException {
		String r = "";
		
		//check inputs exist
		if(!matrixFile.exists()){
			r += "(!)MATRIX file does not exist: " + matrixFile.getName() + "\n";
			return(r);
		}
		//no check ext
		//set default output filename
		if(output==null){
			output = new File(ExtensionFileFilter.stripExtension(matrixFile) + "_SCALE.tab"); 
		//check output filename is valid
		}else if( output.isDirectory() ){
			r += "(!)Must indicate file (not a directory) for your output.";
		} else {
			//no check ext
			//check directory
			if(output.getParent()==null){
// 				System.err.println("default to current directory");
			} else if(!new File(output.getParent()).exists()){
				r += "(!)Check output directory exists: " + output.getParent() + "\n";
			}
		}
		
		//validate row&column start indexes
		if(startROW<0){ r += "(!)Row start must not be less than zero\n"; }
		if(startCOL<0){ r += "(!)Column start must not be less than zero\n"; }
		
		return(r);
	}
}