package scriptmanager.cli.BAM_Manipulation;

import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

import scriptmanager.objects.ToolDescriptions;

/**
	BAM_ManipulatioCLIn/BAMRemoveDupCLI
*/
@Command(name = "remove-duplicates", mixinStandardHelpOptions = true,
	description = ToolDescriptions.remove_duplicates_description + "\n"+
		"@|bold **Please run the picard/samtools tools directly:**|@ \n"+
		"@|bold,yellow 'java -jar picard.jar MarkDuplicates I=<in.bam> O=<marked.bam>\n"+
		"samtools view -F 1024 <marked.bam> > <out.bam>'|@",
	version = "ScriptManager "+ ToolDescriptions.VERSION,
	exitCodeOnInvalidInput = 1,
	exitCodeOnExecutionException = 1)
public class BAMRemoveDupCLI implements Callable<Integer> {
	@Override
	public Integer call() throws Exception {
		System.err.println("***Please use the original tool for this job***\n"+
							"\t'java -jar picard.jar MarkDuplicates I=<in.bam> O=<marked.bam>'\n"+
							"\t'samtools view -F 1024 <marked.bam> > <out.bam>'" );
		System.exit(1);
		return(1);
	}
}