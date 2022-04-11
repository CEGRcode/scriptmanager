package objects;

import java.io.File;
import java.io.PrintStream;

public class PileupParameters {
	//Directory to save matrix and composite into
	private File OUTPUT = null;
	//Composite values file if output
	private PrintStream COMPOSITE = null;
	
	//Read aspect:
	//  0=5prime end, 1=3prime end, 2=midpoint, 3=fullfragment
	private int ASPECT = 0;
	//Read type:
	//  0=read1, 1=read2, 2=allreads
	private int READ = 0;
	
	//Strand type:
	//  0=separate, 1=combined
	private int STRAND = 0;
	
	//Transformation/smoothing type:
	//  0=no_smooth, 1=window, 2=gaussian
	private int TRANS = 0;
	//TRANS=1 parameters:  window size (#bins)
	private int SMOOTH = 0;
	//TRANS=2 parameters:  stdev window size (#bins) and number of standard devations
	private int STDSIZE = 0;
	private int STDNUM = 0;	
	
	private int SHIFT = 0;
	private int BIN = 1;
	private int CPU = 1;
	private int OUTTYPE = 0;			//0=no output, 1=TAB, 2=CDT
	private File BLACKLIST = null;
	private boolean STANDARD = false;
	private boolean outputCOMPOSITE = false;  //Output composite values
	private boolean outputGZIP = false;
	private boolean requirePE = false;
	private double STANDRATIO = 1;
	
	private int MIN_INSERT = -9999;
	private int MAX_INSERT = -9999;
	
	public void printAll(){
		System.out.println( "<><><><><><><><><><><><><><><><><><><><>" );
		System.out.println( "private File OUTPUT = " + OUTPUT );
		System.out.println( "private String COMPOSITE = " + COMPOSITE );
		System.out.println( "private int READ = " + READ );
		System.out.println( "private int ASPECT = " + ASPECT );
		System.out.println( "private int STRAND = " + STRAND );
		System.out.println( "private int TRANS = " + TRANS );
		System.out.println( "private int SHIFT = " + SHIFT );
		System.out.println( "private int BIN = " + BIN );
		System.out.println( "private int SMOOTH = " + SMOOTH );
		System.out.println( "private int STDSIZE = " + STDSIZE );
		System.out.println( "private int STDNUM = " + STDNUM );
		System.out.println( "private int CPU = " + CPU );
		System.out.println( "private int OUTTYPE = " + OUTTYPE );
		System.out.println( "private File BLACKLIST = " + BLACKLIST );
		System.out.println( "private boolean STANDARD = " + STANDARD );
		System.out.println( "private boolean outputCOMPOSITE = " + outputCOMPOSITE );
		System.out.println( "private boolean outputGZIP = " + outputGZIP );
		System.out.println( "private boolean requirePE = " + requirePE );
		System.out.println( "private double STANDRATIO = " + STANDRATIO );
		System.out.println();
		System.out.println( "private int MIN_INSERT = " + MIN_INSERT );
		System.out.println( "private int MAX_INSERT = " + MAX_INSERT );
		System.out.println();
		System.out.println( "<><><><><><><><><><><><><><><><><><><><>" );
	}
	
	public boolean getOutputGZIP() {
		return outputGZIP;
	}
	public void setGZIPstatus(boolean status) {
		outputGZIP = status;
	}
	
	public File getBlacklist() {
		return BLACKLIST;
	}
	public void setBlacklist(File black) {
		BLACKLIST = black;
	}
	
	public int getMaxInsert() {
		return MAX_INSERT;
	}
	public void setMaxInsert(int max) {
		MAX_INSERT = max;
	}
	
	public int getMinInsert() {
		return MIN_INSERT;
	}
	public void setMinInsert(int min) {
		MIN_INSERT = min;
	}
	
	public boolean getPErequire() {
		return requirePE;
	}
	public void setPErequire(boolean status) {
		requirePE = status;
	}
	
	public PrintStream getCompositePrintStream() {
		return COMPOSITE;
	}
	public void setCompositePrintStream(PrintStream comp) {
		COMPOSITE = comp;
	}
	
	public boolean getOutputCompositeStatus() {
		return outputCOMPOSITE;
	}
	public void setOutputCompositeStatus(boolean out) {
		outputCOMPOSITE = out;
	}
	
	public double getRatio() {
		return STANDRATIO;
	}
	public void setRatio(double rat) {
		STANDRATIO = rat;
	}
	
	public boolean getStandard() {
		return STANDARD;
	}
	public void setStandard(boolean stand) {
		STANDARD = stand;
	}
	
	public int getOutputType() {
		return OUTTYPE;
	}
	public void setOutputType(int newtype) {
		OUTTYPE = newtype;
	}
	
	public File getOutputDirectory() {
		return OUTPUT;
	}
	public void setOutputDirectory(File oUTPUT) {
		OUTPUT = oUTPUT;
	}

	public int getAspect() {
		return ASPECT;
	}
	public void setAspect(int aSPECT) {
		ASPECT = aSPECT;
	}

	public int getRead() {
		return READ;
	}
	public void setRead(int rEAD) {
		READ = rEAD;
	}

	public int getStrand() {
		return STRAND;
	}
	public void setStrand(int sTRAND) {
		STRAND = sTRAND;
	}

	public int getTrans() {
		return TRANS;
	}
	public void setTrans(int tRANS) {
		TRANS = tRANS;
	}

	public int getShift() {
		return SHIFT;
	}
	public void setShift(int sHIFT) {
		SHIFT = sHIFT;
	}

	public int getBin() {
		return BIN;
	}
	public void setBin(int bIN) {
		BIN = bIN;
	}

	public int getSmooth() {
		return SMOOTH;
	}
	public void setSmooth(int sMOOTH) {
		SMOOTH = sMOOTH;
	}

	public int getStdSize() {
		return STDSIZE;
	}
	public void setStdSize(int sTDSIZE) {
		STDSIZE = sTDSIZE;
	}

	public int getStdNum() {
		return STDNUM;
	}
	public void setStdNum(int sTDNUM) {
		STDNUM = sTDNUM;
	}

	public int getCPU() {
		return CPU;
	}
	public void setCPU(int cPU) {
		CPU = cPU;
	}
	
	
	public String getCLIcmd(){
		
		String cliCommand = "java -jar ScriptManager.jar read-analysis tag-pileup <bed-file> <bam-file>";
		
		//Add READ
		if(READ==0){ cliCommand += " -1"; }
		else if(READ==1){ cliCommand += " -2"; }
		else if(READ==2){ cliCommand += " -a"; }
		else if(READ==3){ cliCommand += " -m"; }
		else{ System.err.println("This should not print."); }
		//Add STRAND
		if(STRAND==0){ cliCommand += " --separate"; }
		else if(STRAND==1){ cliCommand += " --combined"; }
		else{ System.err.println("This should not print."); }
		//Add TRANS
		if(TRANS==0){
			cliCommand += " --no-smooth"; 
		}else if(TRANS==1){
			if(SMOOTH==3){ cliCommand += " -w"; }
			else{ cliCommand += " -W " + SMOOTH; }
		}else if(TRANS==2){
			if(STDSIZE==5 && STDNUM==3){ cliCommand += " -g"; }
			else{ cliCommand += " -G " + STDSIZE + " " + STDNUM; }
		}else{ System.err.println("This should not print."); }
		
		//Add SHIFT
		if(SHIFT!=0){ cliCommand += " -s " + SHIFT; }
		//Add BIN
		if(BIN!=1){ cliCommand += " -b " + BIN; }
		
		//Add STANDARD
		if(STANDARD==true){ cliCommand += " -t"; }
		//Add BLACKLIST
		if(BLACKLIST!=null){ cliCommand += " -f " + BLACKLIST; }
		
		//Add requirePE
		if(requirePE==true){ cliCommand += " -p"; }
		//Add MIN_INSERT
		if(MIN_INSERT!=-9999){ cliCommand += " -n " + MIN_INSERT; }
		//Add MAX_INSERT
		if(MAX_INSERT!=-9999){ cliCommand += " -x " + MAX_INSERT; }

		//Add outputGZIP
		if(outputGZIP==true){ cliCommand += " -z"; }
		
		//Add CPU
		if(CPU!=1){ cliCommand += " --cpu " + CPU; }
		
		return(cliCommand);
	}
	
}
