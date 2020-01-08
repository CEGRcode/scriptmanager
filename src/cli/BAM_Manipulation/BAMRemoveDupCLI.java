package cli.BAM_Manipulation;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import java.io.File;

/**
	BAM_ManipulatioCLIn/BAMRemoveDupCLI
*/
@Command(name = "remove-duplicates", mixinStandardHelpOptions = true,
		description = "Removes duplicate reads in Paired-End sequencing given identical 5' read locations. RAM intensive process. If program freezes, increase JAVA heap size")
public class BAMRemoveDupCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "The BAM file from which we remove duplicates.")
	private File bamFile;
	
	@Option(names = {"-o", "--output"}, description = "Specify output file ")
	private File output = new File("output.bam");
	@Option(names = {"-n", "--no-bai"}, description = "suppress re-generating BAI index for new BAM file.")
	private boolean generateBAI = true;
	
	@Override
	public Integer call() throws Exception {
		System.out.println( ">BAMRemoveDupCLI.call()" );
// 		SEStats stat = new SEStats( bamFile, output );		
		return(0);
	}
	
}
