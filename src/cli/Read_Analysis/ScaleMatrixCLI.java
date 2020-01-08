package cli.Read_Analysis;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import java.io.File;

/**
	Read_AnalysisCLI/ScaleMatrixCLI
*/
@Command(name = "scale-matrix", mixinStandardHelpOptions = true,
		description = "Apply a user-specified scaling factor to tab-delimited matrix data")
public class ScaleMatrixCLI implements Callable<Integer> {
	
	@Override
	public Integer call() throws Exception {
		System.out.println( ">ScaleMatrixCLI.call()" );
// 		SEStats stat = new SEStats( bamFile, output );		
		return(0);
	}
	
}
	