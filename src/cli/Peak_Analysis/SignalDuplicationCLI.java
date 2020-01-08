package cli.Peak_Analysis;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.Date;
	
/**
	Peak_AnalysisCLI/SignalDuplicationCLI
*/
@Command(name = "signal-dup", mixinStandardHelpOptions = true,
		description = "Calculate duplication statistics at user-specified regions.")
public class SignalDuplicationCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "The BAM file whose statistics we want.")
	private File bamFile;

	@Option(names = {"-o", "--output"}, description = "specify output file ")
	private File output = new File("output.txt");
	@Option(names = {"-g", "--gff"}, description = "--gff ?????????????????")
	private File genome = new File("sacCer3_cegr");
	@Option(names = {"-w", "--window"}, description = "size of signal window around center in bp (default=100)")
	private int window = 200;
	
	@Override
	public Integer call() throws Exception {
		System.out.println( ">SignalDuplicationCLI.call()" );
// 		SEStats stat = new SEStats( bamFile, output );		
		return(0);
	}
	
}

