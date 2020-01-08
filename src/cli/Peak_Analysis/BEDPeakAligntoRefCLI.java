package cli.Peak_Analysis;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.Date;
	
/**
	Peak_AnalysisCLI/BEDPeakAligntoRefCLI
*/
@Command(name = "peak-align-ref", mixinStandardHelpOptions = true,
		description = "Align BED peaks to Reference BED file creating CDT files for heatmap generation")
public class BEDPeakAligntoRefCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "The BED peak file")
	private File bedFile;

	@Option(names = {"-r", "--reference-bed"}, description = "BED reference file")
	private File genome = new File("sacCer3_cegr");
	@Option(names = {"-o", "--output"}, description = "specify output file ")
	private File output = new File("output.txt");
	
	@Override
	public Integer call() throws Exception {
		System.out.println( ">BEDPeakAligntoRefCLI.call()" );
// 		SEStats stat = new SEStats( bamFile, output );		
		return(0);
	}
	
}