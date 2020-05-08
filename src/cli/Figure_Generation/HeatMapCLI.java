package cli.Figure_Generation;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.awt.Color;

import java.io.File;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import scripts.Figure_Generation.HeatmapPlot;

/**
	Figure_GenerationCLI/HeatMapCLI
*/
@Command(name = "heatmap", mixinStandardHelpOptions = true,
		description = "Generate heatmap using CDT files.")
public class HeatMapCLI implements Callable<Integer> {
		
	@Parameters( index = "0", description = "")
	private File CDT;
	
	@Option(names = {"-c", "--color"}, description = "For custom color: type hexadecimal string to represent colors (e.g. #FF0000 is hexadecimal for red).\n See <http://www.javascripter.net/faq/rgbtohex.htm> for some color options with their corresponding hex strings.\n")
	private String color = null;
	@Option(names = {"--black"}, description = "Use the color black for generating the heatmap")
	private boolean black = false;
	@Option(names = {"--red"}, description = "Use the color red for generating the heatmap")
	private boolean red = false;
	@Option(names = {"--blue"}, description = "Use the color blue for generating the heatmap")
	private boolean blue = false;
	@Option(names = {"-r", "--start-row"}, description = "")
	private int startROW = 1;
	@Option(names = {"-l", "--start-col"}, description = "")
	private int startCOL = 2;
	@Option(names = {"-y", "--height"}, description = "indicate a pixel height for the heatmap (default=600)")
	private int pixelHeight = 600;
	@Option(names = {"-x", "--width"}, description = "indicate a pixel width for the heatmap (default=200)")
	private int pixelWidth = 200;
	@Option(names = {"-z", "--compression"}, description = "choose an image compression type: 1=Treeview, 2=Bicubic, 3=Bilinear, 4=Nearest Neighbor (default=1Treeview)")
	private int compression = 1;
	@Option(names = {"-a", "--absolute-threshold"}, description = "use the specified value for contrast thresholding in the heatmap (default=10)")
	private int absolute = 10;
	@Option(names = {"-p", "--percentile-threshold"}, description = "use the specified percentile value for contrast thresholding in the heatmap (try .95 if unsure)")
	private double percentile = -999;
	@Option(names = {"-o", "--output"}, description = "specify output basename for the outputfile ( _<compression-type>.png will be appended to the name, default=heatmapplot)")
	private File outbasename = new File("heatmapplot");
	
	
	@Override
	public Integer call() throws Exception {
		System.out.println( ">HeatMapCLI.call()" );
		
		if( validateInput()!=0 ){
			System.err.println("Invalid input. Check usage using '-h' or '--help'");
			return(1);
		}
		
		// Assign a color from the -c, --red, --blue, and --black inputs
		Color MAXCOLOR = null;
		if(black){ MAXCOLOR = Color.BLACK; }
		else if(red){ MAXCOLOR = Color.RED; }
		else if(blue){ MAXCOLOR = Color.BLUE; }
		else{ MAXCOLOR = Color.decode(color); }
		
			
		// Assign a scaleType based on "compression" input
		String scaleType = null;
		if(compression==1){ scaleType = "treeview"; }
		else if(compression==2){ scaleType = "bicubic"; }
		else if(compression==3){ scaleType = "bilinear"; }
		else if(compression==4){ scaleType = "neighbor"; }
		
		// Generate Heatmap
		HeatmapPlot script_object = new HeatmapPlot( CDT, MAXCOLOR, startROW, startCOL, pixelHeight, pixelWidth, scaleType, absolute, percentile, outbasename, true );
		script_object.run();
		
		System.out.println( "Image Generated." );		
		return(0);
	}
	
	
	private int validateInput(){
		
		// check compression is a valid input value
		if( compression<1 || compression>4 ){
			System.err.println("!!!Compression must be integer 1-4. Please select from the available compression types!");
			return(1);
		}
		
		//check that not more than one of -c, --red, --blue, --black is indicated
		int color_inst = 0;
		color_inst  = (black) ? 1 : 0;
		color_inst += (red) ? 1 : 0;
		color_inst += (blue) ? 1 : 0;
		color_inst += (color!=null) ? 1 : 0;
		if( color_inst > 1 ){
			System.err.println( "!!! You can only use one of the color selection parameters: black(--black), red(--red), blue(--blue), color(-c)!" );
			return(1);
		}
		
		//check that hex string is formatted appropriately
		if( color != null ){
			Pattern hexColorPat = Pattern.compile("#?[0-9A-F]{6}");
			Matcher m = hexColorPat.matcher( color );
			if( !m.matches() ){
				System.err.println("!!!Color must be formatted as a hexidecimal String!\nExpected input string format: \"#?[0-9A-F]{6}\"");
				return(1);
			}
		}
		
		//check outputbasename is valid
		
		//check pixel ranges are valid
// 		TODECIDE:(do we want to set a min/max?)
		if(pixelHeight<=0){
			System.err.println("!!!Image Height must be a positive integer value! check \"-y\" flag.\"");
			return(1);
		}
		if(pixelWidth<=0){
			System.err.println("!!!Image Width must be a positive integer value! check \"-x\" flag.\"");
			return(1);
		}
		
		//check start row/col are valid
		
		return(0);
	}
	
}
