package scripts.BAM_Statistics;

import htsjdk.samtools.AbstractBAMFileIndex;
import htsjdk.samtools.SAMSequenceRecord;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.ValidationStringency;
import scripts.BAM_Statistics.CorrelationScripts.CorrelationCoord;
import scripts.BAM_Statistics.CorrelationScripts.CorrelationExtract;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
// import javax.swing.JFrame;
// import javax.swing.SpringLayout;
// import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
// import javax.swing.JScrollPane;
// import javax.swing.JTabbedPane;
// import javax.swing.JTable;

import window_interface.BAM_Statistics.BAMGenomeCorrelationOutput;
import org.jfree.chart.ChartPanel;
import charts.HeatMap;

@SuppressWarnings("serial")
public class BAMGenomeCorrelation extends Component {
	
	private Vector<File> bamFiles = null;
	private String[] fileID = null;
	private double[][] MATRIX;
	private ChartPanel HEATMAP;
	private File OUT_BASENAME = null;
	private boolean OUTPUT_STATUS = false;
	private int SHIFT;
	private int BIN;
	private int CPU;
	private int READ;
	private boolean GUI = false;
	
	SamReader reader;
	final SamReaderFactory factory = SamReaderFactory.makeDefault().enable(SamReaderFactory.Option.INCLUDE_SOURCE_IN_RECORDS, SamReaderFactory.Option.VALIDATE_CRC_CHECKSUMS).validationStringency(ValidationStringency.SILENT);
	
	public BAMGenomeCorrelation(Vector<File> input, File o, boolean out, int s, int b, int c, int r){
		//Load in bamFiles
		bamFiles = input;
		fileID = new String[bamFiles.size()];
		//Initialize correlation matrix
		MATRIX = new double[bamFiles.size()][bamFiles.size()];
		//Store the rest of the variables
		OUT_BASENAME = o;
		OUTPUT_STATUS = out;
		SHIFT = s;
		BIN = b;
		CPU = c;
		READ = r;
	}
		
	public void getBAMGenomeCorrelation(BAMGenomeCorrelationOutput gui) throws IOException {
		System.out.println(getTimeStamp());
		// Check BAMs first
		if(!validateBAM()) {
			return;
		}
		// Toggle GUI v CLI status
		if( gui!=null ){ GUI = true; }
		
		//Open Output File
		PrintStream OUT = null;
		File OUT_PNG = null;
		if(OUT_BASENAME!=null) {
			try {
				OUT = new PrintStream(new File( OUT_BASENAME + "Correlation.out"));
				OUT_PNG = new File( OUT_BASENAME + "Correlation.png" );
			}
			catch (FileNotFoundException e) { e.printStackTrace(); }
// 			catch (IOException e) {	e.printStackTrace(); }
		}
	
		//Iterate through all BAM files in Vector
		int counter = 0;
		for(int x = 0; x < bamFiles.size(); x++) {				
			for(int y = 0; y < bamFiles.size(); y++) {
				if(x != y && (x - y) >= 1) {
					MATRIX[x][y] = correlate(bamFiles.get(x), bamFiles.get(y));
					MATRIX[y][x] = MATRIX[x][y];
					//gui.firePropertyChange("bam", counter, counter + 1);
					counter++;
				} else if(x == y) { MATRIX[x][y] = 1; }
			}
		}
		
		//Output correlation matrix
		for(int x = 0; x < bamFiles.size(); x++) {
			fileID[x] = bamFiles.get(x).getName();
			printBoth( OUT, bamFiles.get(x).getName() + "\t" );
		}
		printBoth( OUT, "\n" );
		for(int x = 0; x < MATRIX.length; x++) {
			for(int y = 0; y < MATRIX.length; y++) {
				printBoth( OUT, MATRIX[x][y] + "\t");
			}
			printBoth( OUT, "\n" );
		}
		if(OUT != null) OUT.close();
		
		HEATMAP = HeatMap.createCorrelationHeatmap(fileID, MATRIX, OUT_PNG);
	}
	
	
	
// 	Vector<File> bamFiles = null;
// 	String[] fileID = null;
// 	private File OUTPUT_PATH = null;
// 	private boolean OUTPUT_STATUS = false;
// 	PrintStream OUT = null;
// 	private int SHIFT;
// 	private int BIN;
// 	private int CPU;
// 	private int READ;
// 	
// 	final JLayeredPane layeredPane;
// 	final JTabbedPane tabbedPane;
// 		
// 	public BAMGenomeCorrelation(Vector<File> input, File o, boolean out, int s, int b, int c, int r) {
// 
// 		setTitle("Genome Correlation");
// 		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
// 		setBounds(150, 150, 800, 600);
// 		
// 		layeredPane = new JLayeredPane();
// 		getContentPane().add(layeredPane, BorderLayout.CENTER);
// 		SpringLayout sl_layeredPane = new SpringLayout();
// 		layeredPane.setLayout(sl_layeredPane);
// 		
// 		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
// 		sl_layeredPane.putConstraint(SpringLayout.NORTH, tabbedPane, 6, SpringLayout.NORTH, layeredPane);
// 		sl_layeredPane.putConstraint(SpringLayout.WEST, tabbedPane, 6, SpringLayout.WEST, layeredPane);
// 		sl_layeredPane.putConstraint(SpringLayout.SOUTH, tabbedPane, -6, SpringLayout.SOUTH, layeredPane);
// 		sl_layeredPane.putConstraint(SpringLayout.EAST, tabbedPane, -6, SpringLayout.EAST, layeredPane);
// 		layeredPane.add(tabbedPane);
// 		
// 		bamFiles = input;
// 		fileID = new String[bamFiles.size()];
// 		OUTPUT_PATH = o;
// 		OUTPUT_STATUS = out;
// 		SHIFT = s;
// 		BIN = b;
// 		CPU = c;
// 		READ = r;
// 		
// 	}
	
// 	public void run() throws IOException {
// 		
// 		tabbedPane.addTab("Correlation Plot", HeatMap.createCorrelationHeatmap(fileID, MATRIX));
// 		tabbedPane.addTab("Correlation Data", makeTablePanel(MATRIX));
// 		
// 		//Make frame visible at completion of correlations
// 		this.setVisible(true);
// 		System.out.println(getTimeStamp());
// 	}
	
	public double correlate(File exp1, File exp2) {
		System.out.println("Comparing: " + exp1.getName() + "\t-\t" + exp2.getName());

		//Reflexive pearson correlation requiring only a single pass through each genome
		double Sx = 0;
		double Sxx = 0;
		double Sy = 0;
		double Syy = 0;
		double Sxy = 0;
		double count = 0;
		
		//Hyperthread extraction of genomic windows one chromosome at a time
		Vector<CorrelationCoord> ChromosomeWindows = null;
		//Open BAI index to go chromosome by chromosome
		reader = factory.open(exp1);
		AbstractBAMFileIndex bai = (AbstractBAMFileIndex) reader.indexing().getIndex();
		for(int numchrom = 0; numchrom < bai.getNumberOfReferences(); numchrom++) {
			//Object to keep track of the chromosomal data
			ChromosomeWindows = new Vector<CorrelationCoord>();
			SAMSequenceRecord seq = reader.getFileHeader().getSequence(numchrom);
			//System.out.println("Analyzing: " + seq.getSequenceName());
			
			//Break chromosome into 100kb chunks and assign to independent nodes
			int numwindows = (int) (seq.getSequenceLength() / 100000);
			for(int x = 0; x < numwindows; x++) {
				int start = x * 100000;
				int stop = start + 100000;
				ChromosomeWindows.add(new CorrelationCoord(seq.getSequenceName(), start, stop));
			}
			int finalstart = numwindows * 100000;
			int finalstop = (seq.getSequenceLength() / BIN) * BIN;
			ChromosomeWindows.add(new CorrelationCoord(seq.getSequenceName(), finalstart, finalstop));
			
			//Reduce CPU requirement is less windows
			int windowSize = ChromosomeWindows.size();
			if(windowSize < CPU) {
				CPU = ChromosomeWindows.size();;
			}
			ExecutorService parseMaster = Executors.newFixedThreadPool(CPU);
			int subset = 0;
			int currentindex = 0;
			for(int x = 0; x < CPU; x++) {
				currentindex += subset;
				if(CPU == 1) subset = windowSize;
				else if(windowSize % CPU == 0) subset = windowSize / CPU;
				else {
					int remainder = windowSize % CPU;
					if(x < remainder ) subset = (int)(((double)windowSize / (double)CPU) + 1);
					else subset = (int)(((double)windowSize / (double)CPU));
				}
				CorrelationExtract nodeextract = new CorrelationExtract(ChromosomeWindows, READ, SHIFT, BIN, currentindex, subset, exp1, exp2);
				parseMaster.execute(nodeextract);
			}
			parseMaster.shutdown();
			while (!parseMaster.isTerminated()) {
			}
						
			for(int x = 0; x < ChromosomeWindows.size(); x++) {
				Sx += ChromosomeWindows.get(x).getSx();
				Sxx += ChromosomeWindows.get(x).getSxx();
				Sy += ChromosomeWindows.get(x).getSy();
				Syy += ChromosomeWindows.get(x).getSyy();
				Sxy += ChromosomeWindows.get(x).getSxy();
				count += ChromosomeWindows.get(x).getCount();
			}
		}
				
		double numerator = 0;
		double denominator = 0;
				
		numerator = Sxy - ((Sx * Sy) / count);
		denominator = Math.sqrt((Sxx - ((Sx * Sx) / count)) * (Syy - ((Sy * Sy / count))));
		
		double correlation = numerator / denominator;
		System.out.println("Correlation: " + correlation);
		return correlation;
	}
	
	private boolean validateBAM() throws IOException {
		//Check if BAI index file exists for all BAM files before we process any of them
		ArrayList<String> chrName = new ArrayList<String>();
		ArrayList<Integer> chrSize = new ArrayList<Integer>();
		for(int x = 0; x < bamFiles.size(); x++) {	
			File XBAM = bamFiles.get(x);			
			File f = new File(XBAM + ".bai");
			if(!f.exists() || f.isDirectory()) {
				if(GUI){ JOptionPane.showMessageDialog(null, "BAI Index File does not exist for: " + XBAM.getName()); }
				else{ System.err.println("BAI Index File does not exist for: " + XBAM.getName()); }
				return false;
			} else {
				reader = factory.open(XBAM);
				AbstractBAMFileIndex bai = (AbstractBAMFileIndex) reader.indexing().getIndex();
				if(x == 0) {
					for (int z = 0; z < bai.getNumberOfReferences(); z++) {
						chrName.add(reader.getFileHeader().getSequence(z).getSequenceName());
						chrSize.add(reader.getFileHeader().getSequence(z).getSequenceLength());
					}
				} else if(bai.getNumberOfReferences() != chrName.size()) {
					if(GUI){ JOptionPane.showMessageDialog(null, "Unequal number of chromosomes from previous: " + XBAM.getName()); }
					else{ System.err.println("BAI Index File does not exist for: " + XBAM.getName()); }
					reader.close();
					bai.close();
					return false;
				} else {
					boolean MATCH = true;
					for (int z = 0; z < bai.getNumberOfReferences(); z++) {
						if(!chrName.get(z).equals(reader.getFileHeader().getSequence(z).getSequenceName())) { MATCH = false; }
						if(!chrSize.get(z).equals(reader.getFileHeader().getSequence(z).getSequenceLength())) { MATCH = false; }
					}
					if(!MATCH) {
						if(GUI){ JOptionPane.showMessageDialog(null, "File contains chromosome size/name which does not match previous: " + XBAM.getName()); }
						else{ System.err.println("BAI Index File does not exist for: " + XBAM.getName()); }
						reader.close();
						bai.close();
						return false;
					}
				}
				bai.close();
			}
		}
		reader.close();
		return true;
	}
		
// 	public JScrollPane makeTablePanel(double[][] MATRIX) {
// 		JTable table = new JTable(MATRIX.length, MATRIX.length);
// 		table.setName("Correlation Matrix");
// 		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
// 		for(int i = 0; i < MATRIX.length; i++) {
// 			for(int j = 0; j < MATRIX.length; j++) {
// 				if(i == j) table.setValueAt(1, i, j);
// 				else if((i - j) >= 1) {
// 					table.setValueAt(MATRIX[i][j], i, j);
// 					table.setValueAt(MATRIX[j][i], j, i);
// 				}
// 			}
// 		}
// 		for(int i = 0; i < bamFiles.size(); i++) table.getColumnModel().getColumn(i).setHeaderValue(bamFiles.get(i).getName());
// 		table.setPreferredSize(table.getPreferredSize());
// 		JScrollPane pane = new JScrollPane(table);
// 		table.setFillsViewportHeight(true);
// 		pane.setPreferredSize(new Dimension(590, 590));
// 		return pane;
// 	}
	
	private static void printBoth( PrintStream p, String line ){
		if(p!=null) p.print(line);
		System.out.print(line);
	}
	
	private static String getTimeStamp() {
		Date date= new Date();
		String time = new Timestamp(date.getTime()).toString();
		return time;
	}
	
	
	public double[][] getMatrix(){ return(MATRIX); }
	
	public ChartPanel getHeatMap(){ return(HEATMAP); }
	
}
