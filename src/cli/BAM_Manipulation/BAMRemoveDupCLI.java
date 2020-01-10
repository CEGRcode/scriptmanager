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
// 		description = "Removes duplicate reads in Paired-End sequencing given identical 5' read locations. RAM intensive process. If program freezes, increase JAVA heap size")
		description = "Removes duplicate reads in Paired-End sequencing given identical 5' read locations.\n@|bold **Please run the picard/samtools tools directly:**|@ \n@|bold,yellow 'java -jar picard.jar MarkDuplicates I=<in.bam> O=<marked.bam>\nsamtools view -F 1024 <marked.bam> > <out.bam>'|@")
public class BAMRemoveDupCLI implements Callable<Integer> {
	
// 	@Parameters( index = "0", description = "The BAM file from which we remove duplicates.")
// 	private File bamFile;
// 	
// 	@Option(names = {"-o", "--output"}, description = "Specify output file ")
// 	private File output = new File("output.bam");
// 	@Option(names = {"-n", "--no-bai"}, description = "suppress re-generating BAI index for new BAM file.")
// 	private boolean generateBAI = true;
	
	@Override
	public Integer call() throws Exception {
		System.out.println( "java -jar picard.jar MarkDuplicates I=<in.bam> O=<marked.bam>\nsamtools view -F 1024 <marked.bam> > <out.bam>" );
		return(0);
	}
	
}
