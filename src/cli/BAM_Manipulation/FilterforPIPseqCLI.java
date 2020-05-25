package cli.BAM_Manipulation;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.lang.NullPointerException;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import util.FASTAUtilities;
import util.ExtensionFileFilter;
import scripts.BAM_Manipulation.FilterforPIPseq;

/**
	BAM_ManipulatioCLIn/FilterforPIPseqCLI
*/
@Command(name = "filter-pip-seq", mixinStandardHelpOptions = true,
		description = "Filter BAM file by -1 nucleotide. Requires genome FASTA file. Note this program does not index the resulting BAM file and user must use appropriate samtools command to generate BAI.")
public class FilterforPIPseqCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "The BAM file from which we filter.")
	private File bamFile;
	@Parameters( index = "1", description = "The reference genome FASTA file.")
	private File genomeFASTA;
	
	@Option(names = {"-o", "--output"}, description = "specify output file (default=<bamFileNoExt>_PSfilter.bam)")
	private File output = null;
	@Option(names = {"-f", "--filter"}, description = "filter by upstream sequence (default seq ='T')")
	private String filterString = "T";
	
	@Override
	public Integer call() throws Exception {
		System.out.println( ">FilterforPIPseqCLI.call()" );
		String validate = validateInput();
		if( validate.compareTo("")!=0 ){
			System.err.println( validate );
			System.err.println("Invalid input. Check usage using '-h' or '--help'");
			return(1);
		}
		
		// Generate Filtered BAM
		FilterforPIPseq script_obj = new FilterforPIPseq(bamFile, genomeFASTA, output, filterString, null);
		script_obj.run();
		
		System.err.println( "BAM Generated." );
		return(0);
	}
	
	//validateInput outline
	private String validateInput() throws IOException {
		String r = "";
		
		//check inputs exist
		if(!bamFile.exists()){
			r += "(!)BAM file does not exist: " + bamFile.getName() + "\n";
		}
		if(!genomeFASTA.exists()){
			r += "(!)FASTA file does not exist: " + genomeFASTA.getName() + "\n";
		}
		if(r.compareTo("")!=0){ return(r); }
		//check input extensions
		ExtensionFileFilter faFilter = new ExtensionFileFilter("fa");
		if(!faFilter.accept(genomeFASTA)){
			r += "(!)Is this a FASTA file? Check extension: " + genomeFASTA.getName() + "\n";
		}
		if("bam".compareTo(ExtensionFileFilter.getExtension(bamFile))!=0){
			r += "(!)Is this a BAM file? Check extension: " + bamFile.getName() + "\n";
		}
		//check BAI exists
		File f = new File(bamFile+".bai");
		if(!f.exists() || f.isDirectory()){
			r += "(!)BAI Index File does not exist for: " + bamFile.getName() + "\n";
		}
		//check FAI exists (generate if not)
		File FAI = new File(genomeFASTA + ".fai");
		if(!FAI.exists() || FAI.isDirectory()) {
			System.err.println("FASTA Index file not found.\nGenerating new one...\n");
			boolean FASTA_INDEX = FASTAUtilities.buildFASTAIndex(genomeFASTA);
		}
		//set default output filename
		if(output==null){
			String NAME = ExtensionFileFilter.stripExtension(bamFile);
			output = new File(NAME + "_PSfilter.bam");
		//check output filename is valid
		}else{
			//check ext
			try{
				if("bam".compareTo(ExtensionFileFilter.getExtension(output))!=0){
					r += "(!)Use BAM extension for output filename. Try: " + ExtensionFileFilter.stripExtension(output) +  ".bam\n";
				}
			} catch( NullPointerException e){ r += "(!)Output filename must have extension: use BAM extension for output filename. Try: " + ExtensionFileFilter.stripExtension(output) +  ".bam\n"; }
			//check directory
				if(output.getParent()==null){
// 					System.err.println("default to current directory");
				} else if(!new File(output.getParent()).exists()){
					r += "(!)Check output directory exists: " + output.getParent() + "\n";
				}
		}
		//check filter string is valid ATCG (maybe N? check code to see if degenerate nt are accepted)
		/* <ADD CODE HERE> */
		
		return(r);
	}
	
}
