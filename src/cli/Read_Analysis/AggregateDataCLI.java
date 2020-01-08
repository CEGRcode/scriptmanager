package cli.Read_Analysis;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import java.io.File;

/**
	Read_AnalysisCLI/AggregateDataCLI
*/
@Command(name = "aggregate-data", mixinStandardHelpOptions = true,
		description = "Compile data from tab-delimited file into matrix according to user-specified metric")
public class AggregateDataCLI implements Callable<Integer> {
	
	@Override
	public Integer call() throws Exception {
		System.out.println( ">AggregateDataCLI.call()" );
// 		SEStats stat = new SEStats( bamFile, output );		
		return(0);
	}
	
}
	