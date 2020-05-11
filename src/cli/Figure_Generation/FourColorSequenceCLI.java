package cli.Figure_Generation;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.util.concurrent.Callable;

import java.awt.Color;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import scripts.Figure_Generation.FourColorPlot;

/**
	Figure_GenerationCLI/FourColorSequenceCLI
*/
@Command(name = "four-color", mixinStandardHelpOptions = true,
		description = "Generate 4Color sequence plot given FASTA file and user-defined RGB colors",
		sortOptions = false)
public class FourColorSequenceCLI implements Callable<Integer> {
	
	@Option(names = {"-f", "--fasta"}, required=true, description = "input FASTA file of sequences to plot")
	private File fastaFile;
	
	@Option(names = {"-o", "--output"}, description = "specify output file ")
	private File output = new File("output_4color.png");
	@Option(names = {"-c", "--colors"}, arity = "4..5", description = "For custom colors: List colors to use for ATGC[N], in that order. Type hexadecimal string to represent colors, e.g. FF0000 is hexadecimal for red.\n(default=A-red,T-green,G-yellow,C-blue,N-gray, if only 4 colors specified, N will be set to gray)\n See <http://www.javascripter.net/faq/rgbtohex.htm> for some color options with their corresponding hex strings.")
	private ArrayList<String> colors = null;
	@Option(names = {"-x", "--pixel-width"}, description = "pixel width (default=1)")
	private int pixelWidth = 1;
	@Option(names = {"-y", "--pixel-height"}, description = "pixel height (default=1)")
	private int pixelHeight = 1;
	
	@Override
	public Integer call() throws Exception {
		System.out.println( ">FourColorSequenceCLI.call()" );
		String validate = validateInput();
		if( validate.compareTo("")!=0 ){
			System.err.println( validate );
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
		FourColorPlot.generatePLOT(fastaFile, output, ATCG_COLORS, pixelHeight, pixelWidth);
		
		System.out.println( "Image Generated." );
		return(0);
	}
	
	private String validateInput(){
		String r = "";
		//check colors if custom
		if( colors != null ){
			//check that 4-5 provided
			if( colors.size()!=4 && colors.size()!=5 ){
				r += "(!)There must be 4 or 5 colors specified if using custom colors.";
			}
			//check that hex string is formatted properly
			Pattern hexColorPat = Pattern.compile("#?[0-9A-Fa-f]{6}");
			for( int c=0; c<colors.size(); c++ ){
				String hexStr = colors.get(c);
				Matcher m = hexColorPat.matcher( hexStr );
				if( !m.matches() ){
					r += "(!)Color must be formatted as a hexidecimal String.\n" + hexStr + " is not a valid hex string.\nExpected input string format: \"#?[0-9A-F]{6}\"";
				}
			}
		}
		//set default output filename
// 		String[] out = fastaFile.getName().split("\\.");
// 		new File(OUTPUTPATH + File.separator + out[0] + ".png"
		/* <ADD CODE HERE> */
		//check outputbasename is valid
		/* <ADD CODE HERE> */
		//check pixel ranges are valid
		if(pixelHeight<1){
			r += "(!)Image Height must be a positive integer value! check \"-y\" flag.\"";
		}
		if(pixelWidth<1){
			r += "(!)Image Width must be a positive integer value! check \"-x\" flag.\"";
		}
		
		return(r);
	}
}

