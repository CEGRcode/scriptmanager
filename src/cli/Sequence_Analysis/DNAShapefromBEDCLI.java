package cli.Sequence_Analysis;

import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;

import util.ExtensionFileFilter;
import scripts.Sequence_Analysis.DNAShapefromBED;

/**
	Sequence_AnalysisCLI/DNAShapefromBEDCLI
*/
@Command(name = "dna-shape-bed", mixinStandardHelpOptions = true,
		description = "Calculate intrinsic DNA shape parameters given BED file and Genome FASTA file. Based on Roh's lab DNAshape server data.%nNotes: Sequences with Ns are thrown out.",
		sortOptions = false)
public class DNAShapefromBEDCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "reference genome FASTA file")
	private File genomeFile;
	@Parameters( index = "1", description = "the BED file of sequences to extract")
	private File bedFile;
	
	@Option(names = {"-o", "--output-cdt"}, description = "Specify output basename (files for each shape indicated will share this base)")
	private String outputBasename = null;
	@Option(names = {"--avg-composite"}, description = "Save average composite")
	private boolean avgComposite = false;
	@Option(names = {"-f","--force"}, description = "force-strandedness (default)")
	private boolean force = true;
	
	@ArgGroup(validate = false, heading = "Shape Options")
	ShapeType shape = new ShapeType();
	static class ShapeType{
		@Option(names = {"-g","--groove"}, description = "output minor groove width")
		private boolean groove = false;
		@Option(names = {"-r","--roll"}, description = "output roll")
		private boolean roll = false;
		@Option(names = {"-p","--propeller"}, description = "output propeller twist")
		private boolean propeller = false;
		@Option(names = {"-l","--helical"}, description = "output helical twist")
		private boolean helical = false;
		@Option(names = {"-a","--all"}, description = "output groove, roll, propeller twist, and helical twist (equivalent to -grpl).")
		private boolean all = false;
	}
	
	private boolean[] OUTPUT_TYPE = new boolean[]{false,false,false,false};
	
	@Override
	public Integer call() throws Exception {
		System.out.println( ">DNAShapefromBEDCLI.call()" );
		String validate = validateInput();
		if(!validate.equals("")){
			System.err.println( validate );
			System.err.println("Invalid input. Check usage using '-h' or '--help'");
			return(1);
		}
		
		// Generate Composite Plot
		DNAShapefromBED script_obj = new DNAShapefromBED(genomeFile, bedFile, outputBasename, OUTPUT_TYPE, force, new PrintStream[]{null,null,null,null});
		script_obj.run();
		
// 		// Save Composite Plot
// 		JFreeChart chart = CompositePlot.createChart(xydata, title, COLORS);
// 		OutputStream OUT = new FileOutputStream(output);
// 		ChartUtilities.writeChartAsPNG(OUT, chart, pixelWidth, pixelHeight);
		
		// Print Composite Scores
		try {
			if(avgComposite){
				PrintStream COMPOSITE = new PrintStream(new File(outputBasename + "_AVG.out"));
				String[] headers = new String[]{"AVG_MGW","AVG_PropT","AVG_HelT","AVG_Roll"};
				for(int t=0; t<OUTPUT_TYPE.length; t++){
					if(OUTPUT_TYPE[t]){
						double[] AVG = script_obj.getAvg(t);
						// position vals
						for(int z = 0; z < AVG.length; z++) {
							COMPOSITE.print("\t" + z);
						}
						COMPOSITE.print("\n"+ExtensionFileFilter.stripExtension(bedFile)+"_"+headers[t]);
						// score vals
						for(int z = 0; z < AVG.length; z++) {
							COMPOSITE.print("\t" + AVG[z]);
						}
						COMPOSITE.println();
					}
				}
			}
		}catch(FileNotFoundException e){ e.printStackTrace(); }
		
		System.err.println("Shapes Calculated.");
		return(0);
	}
	
	
	private String validateInput() throws IOException {
		String r = "";
		
		//check inputs exist
		if(!genomeFile.exists()){
			r += "(!)FASTA file does not exist: " + genomeFile.getName() + "\n";
		}
		if(!bedFile.exists()){
			r += "(!)BED file does not exist: " + bedFile.getName() + "\n";
		}
		if(!"".equals(r)){ return(r); }
		//check input extensions
		ExtensionFileFilter faFilter = new ExtensionFileFilter("fa");
		if(!faFilter.accept(genomeFile)){
			r += "(!)Is this a FASTA file? Check extension: " + genomeFile.getName() + "\n";
		}
		if(!"bed".equals(ExtensionFileFilter.getExtension(bedFile))){
			r += "(!)Is this a BED file? Check extension: " + bedFile.getName() + "\n";
		}
		//set default output filename
		if(outputBasename==null){
			outputBasename = ExtensionFileFilter.stripExtension(bedFile);
		//check output filename is valid
		}else{
			File outParent = new File(new File(outputBasename).getParent());
			//no extension check
			//check directory
			if(outParent==null){
// 				System.err.println("default to current directory");
			} else if(!outParent.exists()){
				r += "(!)Check output directory exists: " + outParent + "\n";
			}
		}
		
		//Check & set output_type
		if(!(shape.groove || shape.propeller || shape.helical || shape.roll || shape.all)){
			r += "(!)Please select at least one of the shape flags.\n";
		}else if((shape.groove||shape.propeller||shape.helical||shape.roll) && shape.all){
			r += "(!)Please avoid mixing the \"-a\" flag with the other shape flags.\n";
		}
		
		if(shape.groove){ OUTPUT_TYPE[0] = true; }
		if(shape.propeller){ OUTPUT_TYPE[1] = true; }
		if(shape.helical){ OUTPUT_TYPE[2] = true; }
		if(shape.roll){ OUTPUT_TYPE[3] = true; }
		
		if(shape.all){ OUTPUT_TYPE = new boolean[]{true, true, true, true}; }
		
		return(r);
	}
}