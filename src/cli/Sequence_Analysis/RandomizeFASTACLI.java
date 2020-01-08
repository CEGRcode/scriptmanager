package cli.Sequence_Analysis;

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
	Sequence_AnalysisCLI/RandomizeFASTACLI
*/
@Command(name = "randomize-fasta", mixinStandardHelpOptions = true,
		description = "Randomizes FASTA sequence for each input entry")
public class RandomizeFASTACLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "")
	private File bamFile;

	@Option(names = {"-o", "--output"}, description = "specify output file ")
	private File output = new File("output.txt");
	
	@Override
	public Integer call() throws Exception {
		System.out.println( ">RandomizeFASTACLI.call()" );
// 		SEStats stat = new SEStats( bamFile, output );		
		return(0);
	}
	
}
