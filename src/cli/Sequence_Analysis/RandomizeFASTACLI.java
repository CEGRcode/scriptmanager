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

import scripts.Sequence_Analysis.FASTARandomize;

/**
	Sequence_AnalysisCLI/RandomizeFASTACLI
*/
@Command(name = "randomize-fasta", mixinStandardHelpOptions = true,
		description = "Randomizes FASTA sequence for each input entry")
public class RandomizeFASTACLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "the FASTA file ")
	private File fastaFile;

	@Option(names = {"-o", "--output"}, description = "specify output directory (name will be same as original with .gff ext)")
	private File output;
	
	@Override
	public Integer call() throws Exception {
		if( validateInput()!=0 ){ System.err.println("Invalid input. Check usage using '-h' or '--help'"); }
		FASTARandomize.randomizeFASTA(fastaFile, output);
		return(0);
	}
	
	private Integer validateInput(){
		// validation done within BEDtoGFF script
		if( output!=null && !output.isDirectory() ){
			System.err.println("Output must be a directory! Unable to specify specific name at this time.");
			return(1);
		}
		return(0);
	}
}
