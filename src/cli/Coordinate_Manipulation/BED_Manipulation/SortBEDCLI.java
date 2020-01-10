package cli.Coordinate_Manipulation.BED_Manipulation;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

import scripts.Coordinate_Manipulation.BED_Manipulation.SortBED;

/**
	Coordinate_ManipulationCLI/SortBEDCLI
*/
@Command(name = "sort-bed", mixinStandardHelpOptions = true,
	description = "Sort a BED file")
public class SortBEDCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "the BED file to sort")
	private File bedFile;
	
	@Option(names = {"-r", "--reference"}, required = true, description = "the reference CDT file to sort the input by")
	private File cdtReference;
	@Option(names = {"-o", "--output"}, description = "specify output file path with name (no .cdt extension, script will add that)")
	private File output;
	@Option(names = {"-c", "--center"}, description = "sort by center on the input size of expansion in bins (default=100)")
	private int center = -999;
	@Option(names = {"-x", "--index"}, description = "sort by index from the specified start to the specified stop (0-indexed and half-open interval)",
		arity = "2")
	private int[] index = {-999, -999};
	
	private int CDT_SIZE;
	private boolean centerNotIndex;
	private String OUT;
	
	
	@Override
	public Integer call() throws Exception {		
// 		System.err.println("center="+Integer.toString(center));
// 		System.err.println("index="+Integer.toString(index[0]));
// 		System.err.println("\t"+Integer.toString(index[1]));
		
		if( validateInput()!=0 ){
			System.err.println("Invalid input. Check usage using '-h' or '--help'");
			return(1);
		}
		
		if( centerNotIndex ){
			index[0] = (CDT_SIZE / 2) - (center / 2);
			index[1] = (CDT_SIZE / 2) + (center / 2);
		}
		
// 		System.err.println("status="+Boolean.toString(centerNotIndex));
// 		System.err.println("CDT_SIZE="+Integer.toString(CDT_SIZE));
// 		System.err.println("index="+Integer.toString(index[0]));
// 		System.err.println("\t"+Integer.toString(index[1]));
// 		System.err.println("OUT="+OUT);
		
		try{
			SortBED.sortBEDbyCDT(OUT, bedFile, cdtReference, index[0], index[1]);
		} catch (FileNotFoundException e) {
			System.err.println("Check your filenames!");
			e.printStackTrace();
		}
		System.err.println("Expansion Complete");
				
		return(0);
	}
	
	private Integer validateInput(){
		
		int return_val = 0;
		
		// validate CDT as file, with consistent row size, and save row_size value
		try {
			CDT_SIZE = SortBED.parseCDTFile(cdtReference);
		} catch (FileNotFoundException e1) { e1.printStackTrace(); }
		if( CDT_SIZE==-999 ) {
			System.err.println("!!!CDT file doesn't have consistent row sizes!");
			return_val++;
		}
		// validate output file
		if( output==null){     //not specified
			OUT = bedFile.getName().substring(0,bedFile.getName().length() - 4) + "_SORT";
		}else{
			try{
				OUT = output.getCanonicalPath();    //specify directory without filename
				if( output.isDirectory() ){
					OUT = OUT + File.separator + bedFile.getName().substring(0,bedFile.getName().length() - 4) + "_SORT";
				}
			} catch( IOException io ){ io.printStackTrace(); }
		}
		
		// validate center and index sort method options
		centerNotIndex = true;
		// noting specified-- default behavior
		if( center==-999 && index[0]==-999 && index[1]==-999 ) {
			center = 100;
		// both options specified -- bad, return invalid
		} else if( center!=-999 && index[0]!=-999 && index[1]!=-999 ) {
			System.err.println("!!!Must define center sorting OR index sorting, not both.");
			return_val++;
		// center specified
		} else if( center!=-999 && index[0]==-999 && index[1]==-999 ) {
			if( center<0 ){
				System.err.println("!!!Invalid --center input, must be a positive integer value");
				return_val++;
			}
		// index specified
		} else if( center==-999 && index[0]!=-999 && index[1]!=-999 ) {
			centerNotIndex = false;
			if( index[0]<0 ) {
				System.err.println("!!!Invalid --index start input, must be a positive integer value");
				return_val++;
			}
			if( index[1]>CDT_SIZE ) {
				System.err.println("!!!Invalid --index stop input, must be smaller than the CDT row size");
				return_val++;
			}
			if( index[0]>index[1] ) {
				System.err.println("!!!Invalid --index stop must be greater than index start");
				return_val++;
			}
		} else {
			System.err.println("This line should NOT print!");
		}
		
		return(return_val);
	}
	
}