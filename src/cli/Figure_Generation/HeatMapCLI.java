package cli.Figure_Generation;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;
import java.util.List;

import java.io.File;

/**
	Figure_GenerationCLI/HeatMapCLI
*/
@Command(name = "heatmap", mixinStandardHelpOptions = true,
		description = "Generate heatmap using CDT files.")
public class HeatMapCLI implements Callable<Integer> {
		
	@Parameters( index = "0", description = "")
	private File inputFile;
	
	@Option(names = {"-o", "--output"}, description = "specify output file ")
	private File output = new File("output.matrix");
	@Option(names = {"-c", "--color"}, arity = "4..5", description = "list hexadecimal colors to use for ATGCN (e.g. #FF0000 is hexadecimal for red).\n See <http://www.javascripter.net/faq/rgbtohex.htm> to lookup hexadecimal from an RGB color.")
	private List<String> colors;
	@Option(names = {"-a", "--absolute-threshold"}, description = "use the specified value for contrast thresholding in the heatmap (default=10)")
	private int absolute = 10;
	@Option(names = {"-p", "--percentile-threshold"}, description = "use the specified percentile value for contrast thresholding in the heatmap (try .95 if unsure)")
	private double percentile = -9999.0;
	@Option(names = {"-z", "--compression"}, description = "choose an image compression type: 1=Treeview, 2=Bicubic, 3=Bilinear, 4=Nearest Neighbor (default=1Treeview)")
	private int compression = 1;
	
	
	@Override
	public Integer call() throws Exception {
		System.out.println( ">HeatMapCLI.call()" );
// 		SEStats stat = new SEStats( bamFile, output );		
		return(0);
	}
	
}
