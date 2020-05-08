package cli.Figure_Generation;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.awt.Color;

import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import scripts.Figure_Generation.FourColorPlot;

/**
	Figure_GenerationCLI/FourColorSequenceCLI
*/
@Command(name = "four-color", mixinStandardHelpOptions = true,
		description = "Generate 4Color sequence plot given FASTA file and user-defined RGB colors")
public class FourColorSequenceCLI implements Callable<Integer> {
	
	@Option(names = {"-f", "--fasta"}, required=true, description = "input FASTA file of sequences to plot")
	private File fastaFile;
	
	@Option(names = {"-o", "--output"}, description = "specify output file ")
	private File output = new File("output_4color.png");
	@Option(names = {"-c", "--color"}, arity = "4..5", description = "For custom color: List colors to use for ATGC[N], in that order. Type hexadecimal string to represent colors, e.g. FF0000 is hexadecimal for red.\n(default=A-red,T-green,G-yellow,C-blue,N-gray, if only 4 colors specified, N will be set to gray)\n See <http://www.javascripter.net/faq/rgbtohex.htm> for some color options with their corresponding hex strings.")
	private List<String> colors = null;
	@Option(names = {"-y", "--pixel-height"}, description = "pixel height (default=1)")
	private int pixelHeight = 1;
	@Option(names = {"-x", "--pixel-width"}, description = "pixel width (default=1)")
	private int pixelWidth = 1;
	
	@Override
	public Integer call() throws Exception {
		System.out.println( ">FourColorSequenceCLI.call()" );
		
		if( validateInput()!=0 ){
			System.err.println("Invalid input. Check usage using '-h' or '--help'");
			return(1);
		}
		
		// Add colors into ArrayList
		ArrayList<Color> ATCG_COLORS = new ArrayList<Color>(5);
		if( colors!=null ){
			System.out.println("size:"+ Integer.toString(ATCG_COLORS.size()));
			for( int c=0; c<colors.size(); c++){
				ATCG_COLORS.add(c, Color.decode("#"+colors.get(c)) );
			}
		}else{
			ATCG_COLORS.add(0,new Color(208,   0,   0)); //default A-color		#D00000
			ATCG_COLORS.add(1,new Color(  0, 208,   0)); //default T-color		#00D000
			ATCG_COLORS.add(2,new Color(255, 180,   0)); //default G-color		#FFB400
			ATCG_COLORS.add(3,new Color(  0,   0, 208)); //default C-color		#0000D0
		}
		
		// Set N-color to gray if only 4 colors provided as input
		if( colors==null || colors.size()==4 ){
			ATCG_COLORS.add(4,Color.GRAY); //default C-color
		}
		
		
		// Generate Heatmap
// 		FourColorPlot.generatePLOT(File input, File output, ArrayList<Color> COLOR, int h, int w);
		FourColorPlot.generatePLOT(fastaFile, output, ATCG_COLORS, pixelHeight, pixelWidth);
		
		System.out.println( "Image Generated." );
		return(0);
	}
	
	
	private int validateInput(){
		
		//check colors if custom
		if( colors != null ){
			
			//check that 4-5 provided
			if( colors.size()!=4 && colors.size()!=5 ){
				System.err.println("!!!There must be 4 or 5 colors specified if using custom colors!");
				return(1);
			}
			
			//check that hex string is formatted appropriately
			Pattern hexColorPat = Pattern.compile("#?[0-9A-Fa-f]{6}");
			for( int c=0; c<colors.size(); c++ ){
				String hexStr = colors.get(c);
				Matcher m = hexColorPat.matcher( hexStr );
				if( !m.matches() ){
					System.err.println("!!!Color must be formatted as a hexidecimal String!\n" + hexStr + " is not a valid hex string.\nExpected input string format: \"#?[0-9A-F]{6}\"");
					return(1);
				}
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
		
		return(0);
	}
}

