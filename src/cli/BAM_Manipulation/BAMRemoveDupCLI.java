package cli.BAM_Manipulation;

import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

/**
	BAM_ManipulatioCLIn/BAMRemoveDupCLI
*/
@Command(name = "remove-duplicates", mixinStandardHelpOptions = true,
		description = "Removes duplicate reads in Paired-End sequencing given identical 5' read locations.\n"+
			"@|bold **Please run the picard/samtools tools directly:**|@ \n"+
			"@|bold,yellow 'java -jar picard.jar MarkDuplicates I=<in.bam> O=<marked.bam>\n"+
			"samtools view -F 1024 <marked.bam> > <out.bam>'|@")
public class BAMRemoveDupCLI implements Callable<Integer> {
	@Override
	public Integer call() throws Exception {
		System.out.println("***Please use the original tool for this job***\n"+
							"\t'java -jar picard.jar MarkDuplicates I=<in.bam> O=<marked.bam>'\n"+
							"\t'samtools view -F 1024 <marked.bam> > <out.bam>'" );
		return(0);
	}
}