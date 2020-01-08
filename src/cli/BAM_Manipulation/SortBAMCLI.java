package cli.BAM_Manipulation;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import java.io.File;

import scripts.BAM_Manipulation.BAMFileSort;


/**
	BAM_ManipulatioCLI/SortBAMCLI
*/
@Command(name = "sort-bam", mixinStandardHelpOptions = true,
		description = "Sort BAM files in order to efficiently extract and manipulate. RAM intensive process. If program freezes, increase JAVA heap size")
public class SortBAMCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "the BAM file we want to sort")
	private File BAM;
	
	@Option(names = {"-o", "--output"}, description = "specify output file ")
	private File OUT = new File("output.bam");
	
	@Override
	public Integer call() throws Exception {
		System.out.println( ">SortBAMCLI.call()" );
		
		String[] NAME = BAM.getName().split("\\.");
		// Create appropriate File object on defaults
		File OUTPUT;
		if(OUT == null) { OUTPUT = new File(NAME[0] + File.separator + "_sorted.bam"); }
		else if( OUT.isDirectory()) { OUTPUT = new File( OUT.getCanonicalPath() + File.separator + NAME[0] + "_sorted.bam"); }
		else { OUTPUT = OUT; }
		
		BAMFileSort.sort(BAM, OUTPUT);
		System.err.println("Sorting Complete");
			
		return(0);
	}
	
}