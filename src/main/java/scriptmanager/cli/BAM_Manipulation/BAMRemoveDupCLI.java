package scriptmanager.cli.BAM_Manipulation;

import picocli.CommandLine.Command;

import java.io.File;
import java.util.concurrent.Callable;

import scriptmanager.objects.ToolDescriptions;

/**
 * Print a message redirecting user to the original CLI tool.
 * 
 * @author Olivia Lang
 * @see scriptmanager.scripts.BAM_Manipulation.BAMMarkDuplicates
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

	public static String getCLIcommand(File BAM, boolean removeDuplicates, File OUTPUT, File METRICS) {
		String command = "java -jar $PICARD MarkDuplicates";
		command += " INPUT=" + BAM.getAbsolutePath();
		command += " OUTPUT=" + OUTPUT.getAbsolutePath();
		command += " METRICS_FILE=" + METRICS.getAbsolutePath();
		command += removeDuplicates ? "REMOVE_DUPLICATES=true" : "REMOVE_DUPLICATES=false";
		return(command);
	}
}