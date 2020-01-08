package cli.Figure_Generation;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;
import java.util.List;

import java.io.File;

/**
	Figure_GenerationCLI/MergeHeatMapCLI
*/
@Command(name = "merge-heatmap", mixinStandardHelpOptions = true,
		description = "Merge Sense and Antisense png heatmaps")
public class MergeHeatMapCLI implements Callable<Integer> {
	
	@Parameters( index = "0", arity = "1..3", description = "list of PNG files")
	private List<File> inputFile = null;
	
	@Option(names = {"-o", "--output"}, description = "specify output file ")
	private File output = new File("output.matrix");
	
	@Override
	public Integer call() throws Exception {
		System.out.println( ">MergeHeatMapCLI.call()" );
// 		SEStats stat = new SEStats( bamFile, output );		
		return(0);
	}
	
}
