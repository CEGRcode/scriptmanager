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
	Sequence_AnalysisCLI/DNAShapefromFASTACLI
*/
@Command(name = "dna-shape-fasta", mixinStandardHelpOptions = true,
		description = "Calculate intrinsic DNA shape parameters given input FASTA files. Based on Roh's lab DNAshape server data")
public class DNAShapefromFASTACLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "reference genome FASTA file")
	private File genomeFile;
	@Parameters( index = "1", description = "the BED file of sequences to extract")
	private File bedFile;
	
	@Option(names = {"-o", "--output"}, description = "Specify output file ")
	private File output = new File("output.txt");
	@Option(names = {"-f","--force"}, description = "force-strandedness (default)")
	private boolean force = true;
	@Option(names = {"-nf","--no-force"}, description = "don't force-strandedness")
	private boolean noForce = true;
	@Option(names = {"-g","--groove"}, description = "output minor groove width")
	private boolean groove = false;
	@Option(names = {"-r","--roll"}, description = "output roll")
	private boolean roll = false;
	@Option(names = {"-p","--propeller"}, description = "output propeller twist")
	private boolean propeller = false;
	@Option(names = {"-l","--helical"}, description = "output helical twist")
	private boolean helical = false;
	@Option(names = {"-a","--all"}, description = "output groove, roll, propeller twist, and helical twist (equivalent to -grpl).")
	private boolean all = false;
	
	
	@Override
	public Integer call() throws Exception {
		System.out.println( ">DNAShapefromFASTACLI.call()" );
// 		SEStats stat = new SEStats( bamFile, output );		
		return(0);
	}
	
}

