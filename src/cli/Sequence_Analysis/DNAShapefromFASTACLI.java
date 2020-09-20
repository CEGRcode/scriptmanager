package cli.Sequence_Analysis;

import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;

import objects.ToolDescriptions;
import util.ExtensionFileFilter;
//import scripts.Sequence_Analysis.DNAShapefromFASTA;

/**
	Sequence_AnalysisCLI/DNAShapefromFASTACLI
*/
@Command(name = "dna-shape-fasta", mixinStandardHelpOptions = true,
	description = ToolDescriptions.dna_shape_from_fasta_description,
	sortOptions = false,
	exitCodeOnInvalidInput = 1,
	exitCodeOnExecutionException = 1)
public class DNAShapefromFASTACLI implements Callable<Integer> {
	
	@Override
	public Integer call() throws Exception {
		System.err.println( ">DNAShapefromFASTACLI.call()" );
		String validate = validateInput();
		if(!validate.equals("")){
			System.err.println( validate );
			System.err.println("Invalid input. Check usage using '-h' or '--help'");
			System.exit(1);
		}
		
		//SEStats.getSEStats( output, bamFile, null );
		
		//System.err.println("Calculations Complete");
		return(0);
	}
	
	private String validateInput() throws IOException {
		String r = "";
		//validate input here
		//append messages to the user to `r`
		return(r);
	}
}
