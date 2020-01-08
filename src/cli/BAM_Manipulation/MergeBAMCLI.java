package cli.BAM_Manipulation;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import java.io.File;
import java.util.List;

import scripts.BAM_Manipulation.MergeSamFiles;
import scripts.BAM_Manipulation.BAIIndexer;

/**
	BAM_ManipulatioCLIn/MergeBAMCLI
*/
@Command(name = "merge-bam", mixinStandardHelpOptions = true,
		description = "Merges multiple BAM files into a single BAM file. Sorting is performed automatically RAM intensive process. If program freezes, increase JAVA heap size")
public class MergeBAMCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "list the BAM files we want to merge (no limit here but if you're doing a lot of files, keep in mind how much your poor computer can take)",
				arity = "1..*")
	private List<File> BAMS = null;
	
	@Option(names = {"-o", "--output"}, description = "specify output file pathname or directory")
	private File OUT;
	@Option(names = {"-n", "--no-bai"}, description = "suppress re-generating BAI index for the new BAM file.")
	private boolean MAKEBAI = true;
	@Option(names = {"-t", "--threading"}, description = "use threading")
	private boolean THREAD = false;
	
	
	@Override
	public Integer call() throws Exception {
		System.out.println( ">MergeBAMCLI.call()" );
		
		// Create appropriate File object on defaults
		File OUTPUT;
		System.out.println(BAMS.get(0).toString());
		// if(OUT == null) { OUTPUT = new File( BAMS.get(0).getCanonicalParent().toString() + File.separator + "merged_output.bam"); }
// 		else if( OUT.isDirectory()) { OUTPUT = new File( OUT.getCanonicalPath() + File.separator + "merged_output.bam"); }
// 		else { OUTPUT = OUT; }
		// Run the Merge method
// 		MergeSamFiles merge = new MergeSamFiles(BAMS, OUTPUT, THREAD);
//     	merge.run();
    	// Generate BAI file if indicated
//     	if(MAKEBAI) {
// 			BAIIndexer.generateIndex(OUTPUT);
// 		}
		System.err.println("Merging Complete");
		
		return(0);
	}
	
}

