package cli.Peak_Analysis;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.NullPointerException;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

import util.ExtensionFileFilter;
import scripts.Peak_Analysis.SignalDuplication;
	
/**
	Peak_AnalysisCLI/SignalDuplicationCLI
*/
@Command(name = "signal-dup", mixinStandardHelpOptions = true,
		description = "Calculate duplication statistics at user-specified regions.",
		sortOptions = false)
public class SignalDuplicationCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "The GFF file indicating the region in which to calculate duplication statistics")
	private File gffFile;
	@Parameters( index = "1", description = "The BAM file whose statistics we want.")
	private File bamFile;
	
// 	@Option(names = {"-i", "--png"}, description = "The PNG file is saved with the same output filename (with .png extension)")
// 	private boolean png = false;
// 	@Option(names = {"-x", "--pixel-length"}, description = "indicate a pixel width for the plot (default=500)")
// 	private int pixelWidth = 500;
// 	@Option(names = {"-y", "--pixel-height"}, description = "indicate a pixel height for the plot (default=270)")
// 	private int pixelHeight = 270;
	
	@Option(names = {"-o", "--output"}, description = "Specify output filename (default = <bamFile>_sigDup.txt)")
	private File output = null;
	@Option(names = {"-w", "--window"}, description = "size of signal window around center in bp (default=100)")
	private int window = 100;
	
	private File pngFilename = null;
	private PrintStream outPrintStream = null;
	
	@Override
	public Integer call() throws Exception {
		System.err.println( ">SignalDuplicationCLI.call()" );
		String validate = validateInput();
		if(!validate.equals("")){
			System.err.println( validate );
			System.err.println("Invalid input. Check usage using '-h' or '--help'");
			return(1);
		}
		
		SignalDuplication script_obj = new SignalDuplication(gffFile, bamFile, window, outPrintStream);
		script_obj.run();
		
// 		if(png) {
// 			JFreeChart chart = script_obj.getLineChart();
// 			ChartUtilities.writeChartAsPNG(new FileOutputStream(pngFilename), chart, pixelWidth, pixelHeight);
// 		}
		
		System.err.println( "Calculation Complete." );
		return(0);
	}
	
	private String validateInput() throws IOException, NullPointerException {
		String r = "";
		
		//check inputs exist
		if(!bamFile.exists()){
			r += "(!)BAM file does not exist: " + bamFile.getName() + "\n";
		}
		if(!gffFile.exists()){
			r += "(!)GFF file does not exist: " + gffFile.getName() + "\n";
		}
		if(!r.equals("")){ return(r); }
		//check input extensions
		if(!"bam".equals(ExtensionFileFilter.getExtension(bamFile))){
			r += "(!)Is this a BAM file? Check extension: " + bamFile.getName() + "\n";
		}
		if(!"gff".equals(ExtensionFileFilter.getExtension(gffFile))){
			r += "(!)Is this a GFF file? Check extension: " + gffFile.getName() + "\n";
		}
		//check BAI exists
		File f = new File(bamFile+".bai");
		if(!f.exists() || f.isDirectory()){
			r += "(!)BAI Index File does not exist for: " + bamFile.getName() + "\n";
		}
		//set default output filename
		if(output==null){
			output = new File(ExtensionFileFilter.stripExtension(bamFile) + "_sigDup.txt");
		//check output filename is valid
		}else{
			//no check ext
			//check directory
			if(output.getParent()==null){
// 				System.err.println("default to current directory");
			} else if(!new File(output.getParent()).exists()){
				r += "(!)Check output directory exists: " + output.getParent() + "\n";
			}
			output = new File(output.getCanonicalPath()+".txt");
		}
		outPrintStream = new PrintStream(output);
		System.err.println(output);
		//set png file to write to if flagged
// 		if(png){
// 			pngFilename = new File(output.getCanonicalPath() + ".png");
// 		}
		
		//check window size
		if( window<1 ){
			r += "(!)Window size needs to be a positive integer.\n";
		}
		
		return(r);
	}
}