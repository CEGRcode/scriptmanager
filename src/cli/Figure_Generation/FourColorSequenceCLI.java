package cli.Figure_Generation;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import java.io.File;
import java.util.List;

/**
	Figure_GenerationCLI/FourColorSequenceCLI
*/
@Command(name = "four-color", mixinStandardHelpOptions = true,
		description = "Generate 4Color sequence plot given FASTA file and user-defined RGB colors")
public class FourColorSequenceCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "input FASTA file of sequences to plot")
	private File fastaFile;
	
	@Option(names = {"-o", "--output"}, description = "specify output file ")
	private File output = new File("output.matrix");
	@Option(names = {"-c", "--color"}, arity = "4..5", description = "List hexadecimal colors to use for ATGCN (e.g. #FF0000 is hexadecimal for red).\n See <http://www.javascripter.net/faq/rgbtohex.htm> to lookup hexadecimal from an RGB color.")
	private List<String> colors;
	@Option(names = {"-l", "--pixel-height"}, description = "pixel height (default=1)")
	private int height = 1;
	@Option(names = {"-w", "--pixel-width"}, description = "pixel width (default=1)")
	private int width = 1;
	
	@Override
	public Integer call() throws Exception {
		System.out.println( ">FourColorSequenceCLI.call()" );
// 		SEStats stat = new SEStats( bamFile, output );		
		return(0);
	}
	
}

