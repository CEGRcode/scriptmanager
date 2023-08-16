package scriptmanager.cli.Read_Analysis;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import java.io.File;
import java.io.IOException;

import scriptmanager.objects.ToolDescriptions;
import scriptmanager.util.ExtensionFileFilter;
import scriptmanager.scripts.Read_Analysis.TransposeMatrix;

/**
	Read_AnalysisCLI/TransposeMatrixCLI
*/
@Command(name = "transpose-matrix", mixinStandardHelpOptions = true,
	description = ToolDescriptions.scale_matrix_description,
	version = "ScriptManager "+ ToolDescriptions.VERSION,
	sortOptions = false,
	exitCodeOnInvalidInput = 1,
	exitCodeOnExecutionException = 1)
public class TransposeMatrixCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "Input matrix file")
	private File matrixFile;
	
	@Option(names = {"-o", "--output"}, description = "Specify output file (default = <matrixFilename>_TRANPOSE.tab)")
	private File output = null;
	@Option(names = {"-r", "--start-row"}, description = "Row to start transposing the matrix (zero indexed)")
	private int startROW = 1;
	@Option(names = {"-l", "--start-col"}, description = "Column to start transposing the matrix (zero indexed)")
	private int startCOL = 2;
	@Option(names = {"-z", "--compression"}, description = "Output compressed file" )
	private boolean gzOutput = false;
	
	
	@Override
	public Integer call() throws Exception {
		System.err.println( ">TransposeMatrixCLI.call()" );
		String validate = validateInput();
		if(!validate.equals("")){
			System.err.println( validate );
			System.err.println("Invalid input. Check usage using '-h' or '--help'");
			System.exit(1);
		}		
		
		TransposeMatrix script_obj = new TransposeMatrix(matrixFile, output, startROW, startCOL, gzOutput);
		script_obj.run();
		
		System.err.println("All Matrices Transposed.");
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
			output = new File(ExtensionFileFilter.stripExtension(matrixFile) + "_TRANPOSE." + ExtensionFileFilter.getExtension(matrixFile)); 
		//check output filename is valid
		}else if( output.isDirectory() ){
			r += "(!)Must indicate file (not a directory) for your output.";
		} else {
			//no check ext
			//check directory
			if(output.getParent()==null){
			//System.err.println("default to current directory");
			} else if(!new File(output.getParent()).exists()){
				r += "(!)Check output directory exists: " + output.getParent() + "\n";
			}
		}

		//Adds .gz extension if needed
		if (gzOutput && !ExtensionFileFilter.getExtension(output).equals("gz")){
			output = new File(output.getAbsolutePath() + ".gz");
		}
		
		//validate row&column start indexes
		if(startROW<0){ r += "(!)Row start must not be less than zero\n"; }
		if(startCOL<0){ r += "(!)Column start must not be less than zero\n"; }
		
		return(r);
	}
}