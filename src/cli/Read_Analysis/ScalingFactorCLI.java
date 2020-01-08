package cli.Read_Analysis;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import java.io.File;

/**
	Read_AnalysisCLI/ScalingFactorCLI
*/
@Command(name = "scaling-factor", mixinStandardHelpOptions = true,
		description = "Calculate the factor as either total tag normalization or normalization of ChIP-seq data with control (PMID:22883957)")
public class ScalingFactorCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "The BAM file from which we remove duplicates.")
	private File bamFile;
	
	@Option(names = {"-o", "--output"}, description = "Specify output file ")
	private File output = new File("output.bam");
	@Option(names = {"-f", "--blacklist"}, description = "specify blacklist file to filter by")
	private File blacklistFilter = null;
	@Option(names = {"-c", "--control"}, description = "control BAM file")
	private File controlBAM = null;
	@Option(names = {"-t", "--total"}, description = "total tag scaling (default)")
	private int total = -1;
	@Option(names = {"-n", "-ncis"}, arity = "2", description = "ncis normalization with window size in bp and unitless minimum fraction (default-size=500, default-fraction=0.75)")
	private int[] ncisInputs = new int[]{-9999,-9999};
	@Option(names = {"-nt", "-ncis-total"}, arity = "2", description = "ncis with total tag (default-size=500, default-fraction=0.75)")
	private int[] ncisTotalInputs = new int[]{-9999,-9999};
	
	@Override
	public Integer call() throws Exception {
		System.out.println( ">ScalingFactorCLI.call()" );
// 		SEStats stat = new SEStats( bamFile, output );		
		return(0);
	}
	
}
