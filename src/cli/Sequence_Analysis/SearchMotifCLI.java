package cli.Sequence_Analysis;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import java.io.File;
import java.io.IOException;

import util.ExtensionFileFilter;
import scripts.Sequence_Analysis.SearchMotif;

/**
	Sequence_AnalysisCLI/SearchMotifCLI
*/
@Command(name = "search-motif", mixinStandardHelpOptions = true,
		description = "Search for an IUPAC DNA sequence motif in FASTA files with mismatches allowed",
		sortOptions = false)
public class SearchMotifCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "The FASTA file in which to search for the motif.")
	private File fastaFile;
	
	@Option(names = {"-o", "--output"}, description = "specify output file")
	private File output = null;
	@Option(names = {"-m", "--motif"}, required=true, description = "the IUPAC motif to search for")
	private String motif;
	@Option(names = {"-n", "--mismatches"}, description = "the number of mismatches allowed (default=0)")
	private int ALLOWED_MISMATCH = 0;
	
	@Override
	public Integer call() throws Exception {
		System.out.println( ">SearchMotifCLI.call()" );
		String validate = validateInput();
		if(!validate.equals("")){
			System.err.println( validate );
			System.err.println("Invalid input. Check usage using '-h' or '--help'");
			return(1);
		}
		
		SearchMotif script_obj = new SearchMotif(fastaFile, motif, ALLOWED_MISMATCH, output, null);
		script_obj.run();
		
		System.err.println("Search Complete.");
		return(0);
	}
	
	private String validateInput() throws IOException {
		String r = "";
		
		//check inputs exist
		if(!fastaFile.exists()){
			r += "(!)FASTA file does not exist: " + fastaFile.getName() + "\n";
			return(r);
		}
		//check input extensions
		ExtensionFileFilter faFilter = new ExtensionFileFilter("fa");
		if(!faFilter.accept(fastaFile)){
			r += "(!)Is this a FASTA file? Check extension: " + fastaFile.getName() + "\n";
		}
		//set default output filename
		if(output==null){
			output = new File(motif + "_" + Integer.toString(ALLOWED_MISMATCH) + "Mismatch_" + ExtensionFileFilter.stripExtension(fastaFile) + ".bed");
		//check output filename is valid
		}else{
			//check ext
			try{
				if(!"bed".equals(ExtensionFileFilter.getExtension(output))){
					r += "(!)Use BED extension for output filename. Try: " + ExtensionFileFilter.stripExtension(output) + ".bed\n";
				}
			} catch( NullPointerException e){ r += "(!)Output filename must have extension: use BED extension for output filename. Try: " + output + ".bed\n"; }
			//check directory
			if(output.getParent()==null){
// 				System.err.println("default to current directory");
			} else if(!new File(output.getParent()).exists()){
				r += "(!)Check output directory exists: " + output.getParent() + "\n";
			}
		}
		
		//check mismatch value
		if(ALLOWED_MISMATCH<0){ r += "(!)Please use a non-negative integer for allowed mismatches."; }
		
		return(r);
	}
}