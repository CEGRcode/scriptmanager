package scriptmanager.cli.BAM_Manipulation;

import picocli.CommandLine.Command;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import scriptmanager.objects.ToolDescriptions;

/**
 * Prints a message redirecting user to the original CLI tool (Picard
 * {@link picard.sam.markduplicates.MarkDuplicates})
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

	/**
	 * Creates a new BAMRemoveDupCLI object
	 */
	public BAMRemoveDupCLI(){}

	/**
	 * Runs when this subcommand is called, running script in respective script package with user defined arguments
	 * @throws IOException Invalid file or parameters
	 */
	@Override
	public Integer call() throws Exception {
		System.err.println("***Please use the original tool for this job***\n"+
							"\t'java -jar picard.jar MarkDuplicates I=<in.bam> O=<marked.bam>'\n"+
							"\t'samtools view -F 1024 <marked.bam> > <out.bam>'" );
		System.exit(1);
		return(1);
	}

	/**
	 * Returns picard command for generating running MarkDuplicates 
	 * @param BAM BAM file ot be marked
	 * @param removeDuplicates If duplicate reads should be removed
	 * @param OUTPUT Ouput BAM file
	 * @param METRICS .metricts file for outputting stats
	 * @return Picard command for running MarkDuplicates 
	 */
	public static String getCLIcommand(File BAM, boolean removeDuplicates, File OUTPUT, File METRICS) {
		String command = "java -jar $PICARD MarkDuplicates";
		command += " INPUT=" + BAM.getAbsolutePath();
		command += " OUTPUT=" + OUTPUT.getAbsolutePath();
		command += " METRICS_FILE=" + METRICS.getAbsolutePath();
		command += removeDuplicates ? "REMOVE_DUPLICATES=true" : "REMOVE_DUPLICATES=false";
		return(command);
	}
}