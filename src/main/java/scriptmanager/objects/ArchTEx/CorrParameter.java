package scriptmanager.objects.ArchTEx;

//import java.io.File;
//import java.util.Vector;

/**
 * Object to help store ArchTEx Cross-Correlation parameter values.
 * <br>
 * Code largely sourced from ArchTEx.analysis.corr.CorrParameter in <a href=
 * "https://github.com/WilliamKMLai/ArchTEx">https://github.com/WilliamKMLai/ArchTEx</a>
 * 
 * @author William KM Lai
 * @see scriptmanager.scripts.BAM_Statistics.ArchTExCrossCorrelation
 * @see scriptmanager.window_interface.BAM_Statistics.ArchTExCrossCorrelationOutput
 * @see scriptmanager.window_interface.BAM_Statistics.ArchTExCrossCorrelationWindow
 */
public class CorrParameter {
//	//BAM Files and Accompanying indexes
//	private Vector<File> input;	// Stores the names of input files
//	private Vector<File> index;		// Stores the name of the index file

	//Parameter to determine between random sampling and complete sampling
	private boolean corrType = false; //Default randomly sample genome

	//Random Sampling Parameters
	private int windowSize = 50000;	//Default set Window frame for each extraction to 50kb
	private int iterations = 10;	//Default set number of random iterations per chromosome

	//TODO if time add resolution and window frame variables
	private int resolution = 1;	//Default set correlation resolution
	private int corrWindow = 1001; //Default set window size of correlation

	//Misc Parameters
	private int threads = 1;	//Default set number of cpu's to use to 1
//	private int currentExp = 0;	//Default current experiment under analysis
//
//	public CorrParameter() {
//		input = new Vector<File>();
//		index = new Vector<File>();
//	}
//
//	public void setExp(int newexp) {
//		currentExp = newexp;
//	}
//	
//	public int getExp() {
//		return currentExp;
//	}
	
	public boolean getCorrType() {
		return corrType;
	}
	
	public void setCorrType(boolean newtype) {
		corrType = newtype;
	}
	
//	public Vector<File> getInput() {
//		return input;
//	}
//
//	public void setInput(Vector<File> newinput) {
//		input = newinput;
//	}
//
//	public File getInputIndex(int index) {
//		return input.get(index);
//	}
//
//	public void addInput(File newinput) {
//		input.add(newinput);
//	}
//
//	public Vector<File> getIndex() {
//		return index;
//	}
//
//	public void setIndex(Vector<File> newindex) {
//		index = newindex;		
//	}
//
//	public File getIndexIndex(int in) {
//		return index.get(in);		
//	}
//
//	public void addIndex(File newinput) {
//		index.add(newinput);		
//	}

	public int getWindow() {
		return windowSize;		
	}

	public void setWindow(int newwindow) {
		windowSize = newwindow;		
	}

	public int getIterations() {
		return iterations;		
	}

	public void setIterations(int newiter) {
		iterations = newiter;		
	}

	public int getResolution() {
		return resolution;		
	}

	public void setResolution(int newresolution) {
		resolution = newresolution;		
	}

	public int getCorrWindow() {
		return corrWindow;		
	}

	public void setCorrWindow(int newcorrWindow) {
		corrWindow = newcorrWindow;		
	}

	public int getThreads() {
		return threads;		
	}

	public void setThreads(int newthreads) {
		threads = newthreads;		
	}

	public String printStats() {
		String OUTPUT = "";
//		if(input == null)	OUTPUT += "Input file: NONE\n";
//		else {
//			for(int x = 0; x < input.size(); x++) {
//				OUTPUT += "Input file: " + input.get(x) + "\n";
//			}
//		}
//		if(index == null)	OUTPUT += "Index file: NONE\n";
//		else {
//			for(int x = 0; x < index.size(); x++) {
//				OUTPUT += "Index file: " + input.get(x) + "\n";
//			}
//		}
		OUTPUT += "Window Size: " + windowSize + "bp" + "\n";
		OUTPUT += "Iterations: " + iterations + "\n";
		OUTPUT += "Resolution: " + resolution + "bp" + "\n";
		OUTPUT += "CPU Cores: " + threads;
		return OUTPUT;
	}

//	public String[] getInputNames() {
//		String[] names = new String[input.size()];
//		for (int x = 0; x < names.length; x++) {
//			names[x] = input.get(x).getName();
//		}
//		return names;
//	}
//
//	public String[] getIndexNames() {
//		String[] names = new String[input.size()];
//		for (int x = 0; x < names.length; x++) {
//			names[x] = input.get(x).getName();
//		}
//		return names;
//	}
}