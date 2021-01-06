package cli.Figure_Generation;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.awt.Color;
import java.lang.NullPointerException;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.XYSeries;

import charts.CompositePlot;
import objects.ToolDescriptions;
import util.ExtensionFileFilter;
import scripts.Figure_Generation.MergeHeatMapPlot;

/**
	Figure_GenerationCLI/CompositePlotCLI
*/
@Command(name = "composite-plot", mixinStandardHelpOptions = true,
	description = ToolDescriptions.composite_description,
	sortOptions = false,
	exitCodeOnInvalidInput = 1,
	exitCodeOnExecutionException = 1)
public class CompositePlotCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "Composite data to plot. (formatted like TagPileup composite output)")
	private File compositeData;
	
	@Option(names = {"-o", "--output"}, description = "specify output filename, please use PNG extension\n(default=Input filename with \"_compositePlot.png\" appended to the name in working directory of ScriptManager")
	private File output = null;
	@Option(names = {"-t", "--title"}, description = "set title (default=<composite-file-name>)")
	private String title = null;
	@Option(names = {"-l", "--legend"}, description = "add a legend (default=no legend)")
	private boolean legend = false;
	@Option(names = {"-x", "--width"}, description = "indicate a pixel width for the plot (default=500)")
	private int pixelWidth = 500;
	@Option(names = {"-y", "--height"}, description = "indicate a pixel height for the plot (default=270)")
	private int pixelHeight = 270;
	@Option(names = {"-c", "--custom-colors"}, description = "indicate colors to use for each series. Must indicate a number of colors that matches number of dataseries\n" +
										"default behavior:\n" +
										"if one series input, use black\n" +
										"if two series input, use blue(sense) and red(anti)\n" +
										"if greater than two series, cycle through a set of 20 preset colors.",
					arity="1..")
	private String[] colors = null;
	
	XYSeriesCollection xydata = null;
	private ArrayList<Color> COLORS = new ArrayList<Color>();
	
	//Colors copied from response on StackOverflow:
	// https://stackoverflow.com/questions/470690/how-to-automatically-generate-n-distinct-colors
	String[] KELLY_COLORS_HEX = {
		"0xFFB300", // Vivid Yellow
		"0x803E75", // Strong Purple
		"0xFF6800", // Vivid Orange
		"0xA6BDD7", // Very Light Blue
		"0xC10020", // Vivid Red
		"0xCEA262", // Grayish Yellow
		"0x817066", // Medium Gray
		// The following don't work well for people with defective color vision
		"0x007D34", // Vivid Green
		"0xF6768E", // Strong Purplish Pink
		"0x00538A", // Strong Blue
		"0xFF7A5C", // Strong Yellowish Pink
		"0x53377A", // Strong Violet
		"0xFF8E00", // Vivid Orange Yellow
		"0xB32851", // Strong Purplish Red
		"0xF4C800", // Vivid Greenish Yellow
		"0x7F180D", // Strong Reddish Brown
		"0x93AA00", // Vivid Yellowish Green
		"0x593315", // Deep Yellowish Brown
		"0xF13A13", // Vivid Reddish Orange
		"0x232C16", // Dark Olive Green
    };
	
	@Override
	public Integer call() throws Exception {
		System.err.println( ">CompositePlotCLI.call()" );
		String validate = validateInput();
		if(!validate.equals("")){
			System.err.println( validate );
			System.err.println("Invalid input. Check usage using '-h' or '--help'");
			System.exit(1);
		}
		
		// Generate Composite Plot
		JFreeChart chart = CompositePlot.createChart(xydata, title, COLORS, legend);
		// Save Composite Plot
		OutputStream OUT = new FileOutputStream(output);
		ChartUtilities.writeChartAsPNG(OUT, chart, pixelWidth, pixelHeight);
		
		System.err.println( "Image Generated." );
		return(0);
	}
	
	private String validateInput() throws IOException {
		String r = "";
		
		//check inputs exist
		if(!compositeData.exists()){
			r += "(!)Composite Data file does not exist: " + compositeData.getName() +  "\n";
			return(r);
		}
		//check input extensions
		if(!"out".equals(ExtensionFileFilter.getExtension(compositeData))){
			r += "(!)Is this a \".out\" file? Check extension: " + compositeData.getName() +  "\n";
		}
		//set default output filename
		if(output==null){
			output = new File(ExtensionFileFilter.stripExtension(compositeData) + "_compositePlot.png");
		//check output filename is valid
		}else {
			//check ext
			try{
				if(!"png".equals(ExtensionFileFilter.getExtension(output))){
					r += "(!)Use PNG extension for output filename. Try: " + ExtensionFileFilter.stripExtension(output) +  ".png\n";
				}
			} catch( NullPointerException e){ r += "(!)Output filename must have extension: use PNG extension for output filename. Try: " + ExtensionFileFilter.stripExtension(output) +  ".png\n"; }
			//check directory
			if(output.getParent()==null){
// 				System.err.println("default to current directory");
			} else if(!new File(output.getParent()).exists()){
				r += "(!)Check output directory exists: " + output.getParent() + "\n";
			}
		}
		
		//check pixel ranges are valid
		if(pixelHeight<=0){ r += "(!)Cell height must be a positive integer value! check \"-y\" flag.\""; }
		if(pixelWidth<=0) { r += "(!)Cell width must be a positive integer value! check \"-x\" flag.\""; }
		
		// Parse Datafile
		try{
			xydata = parseData();
			if(xydata==null){ r += "(!)The number of y-values don't match the number of x-values"; }
		} catch (FileNotFoundException e) { e.printStackTrace(); }
		
		// Set Color
		if(colors==null){ //set defaults based on number of dataseries (n=1 -> black, n=2 -> blue,red, n=3 -> cycle through kelly colors)
			if(xydata.getSeriesCount()==1){
				//set color to black default unless otherwise indicated
				COLORS.add(Color.BLACK);
			} else if(xydata.getSeriesCount()==2){
				//set colors to blue and red default unless otherwise indicated
				COLORS.add(Color.BLUE);
				COLORS.add(Color.RED);
			} else if(xydata.getSeriesCount()>2){
				//assign a diverse set of colors (as many as there are series)
				for(int i=0; i<xydata.getSeriesCount(); i++){
					int index = i % KELLY_COLORS_HEX.length;
					COLORS.add(Color.decode(KELLY_COLORS_HEX[index]));
				}
			}
		} else { //set user specified values and check they match number of data series
			//assert custom colors have been assigned and length matches number of series
			if(colors.length!=xydata.getSeriesCount()){
				r += "(!)Number of colors specified("+colors.length+") must match number of dataseries("+xydata.getSeriesCount()+")\n";
			}
			//check color input format and decode
			Pattern hexColorPat = Pattern.compile("[0-9A-Fa-f]{6}");
			for(int i=0; i<colors.length; i++){
				Matcher m = hexColorPat.matcher(colors[i]);
				if(!m.matches()){
					r += "(!)Color(" + colors[i] + ") must be formatted as a hexidecimal String!\n\tExpected input string format: \"[0-9A-Fa-f]{6}\"\n";
				}else{
					System.err.println("Decoding color: 0x" + colors[i]);
					COLORS.add(Color.decode("0x" + colors[i]));
				}
			}
		}
		return(r);
	}
	
	public XYSeriesCollection parseData() throws FileNotFoundException {
		//parse input into XYDataset  obj
		XYSeriesCollection dataset = new XYSeriesCollection();
		Scanner scan = new Scanner(compositeData);
		//parse x values
		String[] tokens = scan.nextLine().split("\t");
		if(!tokens[0].equals("")){
			System.err.println("(!) First row of input file must have an empty first column (as x-values)");
			return null;
		}
		double[] x = new double[tokens.length-1];
		for(int i = 1; i < tokens.length; i++){
			x[i-1] = Double.parseDouble(tokens[i]);
		}
		
		XYSeries s;
		//line-by-line through file
		while (scan.hasNextLine()) {
			tokens = scan.nextLine().split("\t");
			//check for format consistency: number of x-values matches y-values
			if(tokens.length-1!=x.length){
				System.err.println("(!) Check number of x-values matches number of y-values");
				return null;
			}
			//skip any rows with blank labels
			if(tokens[0].equals("")){ 
				for(int i = 1; i < tokens.length; i++){
					if(x[i-1]!=Double.parseDouble(tokens[i])){
						System.err.println(x[i-1]);
						System.err.println(tokens[i]);
						System.err.println("(!) Check dataseries based on same x-scale file");
						return null;
					}
				}
				continue;
			}
			//initialize series based on rowname column of input file
			s = new XYSeries(tokens[0]);
			//parse y-values of current row
			for(int i = 1; i < tokens.length; i++){
				s.add(x[i-1],Double.parseDouble(tokens[i]));
			}
			dataset.addSeries(s);
		}
		scan.close();
		return(dataset);
	}
}