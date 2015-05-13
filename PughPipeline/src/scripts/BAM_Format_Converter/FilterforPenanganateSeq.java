package scripts.BAM_Format_Converter;

import htsjdk.samtools.AbstractBAMFileIndex;
import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMFileWriter;
import htsjdk.samtools.SAMFileWriterFactory;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMSequenceRecord;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import htsjdk.samtools.util.CloseableIterator;
import htsjdk.samtools.util.IOUtil;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import util.LineReader;
import util.FASTAUtilities;

@SuppressWarnings("serial")
public class FilterforPenanganateSeq extends JFrame {
	File bamFile = null;
	File genome = null;
	File output = null;
	String SEQ = "";
	
	boolean FASTA_INDEX = true;
	
	private JTextArea textArea;
	
	public FilterforPenanganateSeq(File in, File gen, File out, String s) {
		setTitle("Permanganate-Seq Filtering Progress");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(150, 150, 600, 800);
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
		
		bamFile = in;
		genome = gen;
		output = out;
		SEQ = s.toUpperCase();
	}
	
	public void run() throws IOException {
		File FAI = new File(genome + ".fai");
		//Check if FAI index file exists
		if(!FAI.exists() || FAI.isDirectory()) {
			textArea.append("FASTA Index file not found.\nGenerating new one...\n");
			FASTA_INDEX = buildFASTAIndex(genome);
		}		
		
		//Check if BAI index file exists
		File f = new File(bamFile + ".bai");
		if(f.exists() && !f.isDirectory()) {
			IndexedFastaSequenceFile QUERY = new IndexedFastaSequenceFile(genome);
			
			IOUtil.assertFileIsReadable(bamFile);
			IOUtil.assertFileIsWritable(output);
			final SamReader reader = SamReaderFactory.makeDefault().open(bamFile);
			reader.getFileHeader().setSortOrder(SAMFileHeader.SortOrder.coordinate);
			final SAMFileWriter writer = new SAMFileWriterFactory().makeSAMOrBAMWriter(reader.getFileHeader(), false, output);
			
			textArea.append(bamFile.getName() + "\n"); //output file name to textarea
			
			//Code to get individual chromosome stats
			AbstractBAMFileIndex bai = (AbstractBAMFileIndex) reader.indexing().getIndex();
			for (int z = 0; z < bai.getNumberOfReferences(); z++) {
				SAMSequenceRecord seq = reader.getFileHeader().getSequence(z);
				System.out.println(seq.getSequenceName());
				textArea.append(seq.getSequenceName() + "\n");
						
				CloseableIterator<SAMRecord> iter = reader.query(seq.getSequenceName(), 0, seq.getSequenceLength(), false);
				while (iter.hasNext()) {
					//Create the record object 
					SAMRecord sr = iter.next();
					if(sr.getReadPairedFlag()) {
						if(sr.getProperPairFlag() && sr.getFirstOfPairFlag()) {
							String filter = "";
							//if on the positive strand
							if(!sr.getReadNegativeStrandFlag()) {
								if(sr.getUnclippedStart() - 1 > 0) { filter = new String(QUERY.getSubsequenceAt(seq.getSequenceName(), sr.getUnclippedStart() - 1, sr.getUnclippedStart() - 1).getBases()); }
							}
							else {
								if(sr.getUnclippedEnd() + 1 <= seq.getSequenceLength()) {
										filter = new String(QUERY.getSubsequenceAt(seq.getSequenceName(), sr.getUnclippedEnd() + 1, sr.getUnclippedEnd() + 1).getBases());
										filter = FASTAUtilities.RevComplement(filter);
								}
							}
							//System.out.println(sr.getReadString() + "\t" + seq.getSequenceName() + "\t" + sr.getUnclippedStart() + "\t" + sr.getUnclippedEnd() + "\t" + sr.getReadNegativeStrandFlag() + "\t" + filter);
							if(filter.toUpperCase().equals(SEQ)) { writer.addAlignment(sr); }							
						}
					} else {
						String filter = "";
						//if on the positive strand
						if(!sr.getReadNegativeStrandFlag()) {
							filter = new String(QUERY.getSubsequenceAt(seq.getSequenceName(), sr.getUnclippedStart() - 1, sr.getUnclippedStart() - 1).getBases());
						}
						else {
							filter = new String(QUERY.getSubsequenceAt(seq.getSequenceName(), sr.getUnclippedEnd() + 1, sr.getUnclippedEnd() + 1).getBases());
							filter = FASTAUtilities.RevComplement(filter);
						}
						//System.out.println(sr.getReadString() + "\t" + seq.getSequenceName() + "\t" + sr.getUnclippedStart() + "\t" + sr.getUnclippedEnd() + "\t" + sr.getReadNegativeStrandFlag() + "\t" + filter);
						if(filter.toUpperCase().equals(SEQ)) { writer.addAlignment(sr); }		
					}
				}
				iter.close();
			}
			QUERY.close();
			writer.close();
			reader.close();
			bai.close();

		} else {
			JOptionPane.showMessageDialog(null, "BAI Index File does not exist for: " + bamFile.getName() + "\n");
		}
	}
	
	//contig_name\tcontig_length\toffset_distance_from_last_contig\tcolumnlength\tcolumnlength_with_endline\n"
    //chr1    230218  6       60      61
    //chr2    813184  234067  60      61
    public boolean buildFASTAIndex(File fasta) throws IOException {
    	textArea.append(getTimeStamp() + "\nBuilding Genome Index...\n");
    	
    	boolean properFASTA = true;
    	ArrayList<String> IMPROPER_FASTA = new ArrayList<String>();
    	int counter = 0;

    	String contig = "";
    	int binaryOffset = 0;
    	int currentOffset = 0;
    	int contigLength = 0;
    	int column_Length = 0;
    	int untrimmed_Column_Length = 0;
    	    	
    	BufferedReader b_read = new BufferedReader(new FileReader(fasta));
    	LineReader reader = new LineReader(b_read);
    	PrintStream FAI = new PrintStream(fasta.getName() + ".fai");
    	
    	String strLine = "";
    	while(!(strLine = reader.readLine()).equals("")) {
    		//Pull parameters line
    		int current_untrimmed_Column_Length = strLine.length();
			int current_column_Length = strLine.trim().length();

			if(strLine.contains(">")) {
				if(IMPROPER_FASTA.size() > 1) {
					textArea.append("Unequal column size FASTA Line at:\n");
					for(int z = 0; z < IMPROPER_FASTA.size(); z++) {	textArea.append(contig + "\t" + IMPROPER_FASTA.get(z) + "\n");	}
					properFASTA = false;
					break;
				}
				if(counter > 0) { FAI.println(contig + "\t" + contigLength + "\t" + currentOffset + "\t" + column_Length + "\t" + untrimmed_Column_Length);	}
				//Reset parameters for new contig
				untrimmed_Column_Length = 0;
				contigLength = 0;
				column_Length = 0;
				contig = strLine.trim().substring(1);
				binaryOffset += current_untrimmed_Column_Length;
				currentOffset = binaryOffset;
				IMPROPER_FASTA = new ArrayList<String>();
			} else {
				if(untrimmed_Column_Length == 0) { untrimmed_Column_Length = current_untrimmed_Column_Length; }
				if(column_Length == 0) { column_Length = current_column_Length;	}
				binaryOffset += current_untrimmed_Column_Length;
				contigLength += current_column_Length;
				
				//Check to make sure all the columns are equal. Index is invalid otherwise
				if(current_untrimmed_Column_Length != untrimmed_Column_Length || current_untrimmed_Column_Length == 0) { IMPROPER_FASTA.add(strLine.trim());	}
			}
			counter++;
    	}
		FAI.println(contig + "\t" + contigLength + "\t" + currentOffset + "\t" + column_Length + "\t" + untrimmed_Column_Length);
		b_read.close();
    	FAI.close();
    	
		if(properFASTA) textArea.append("Genome Index Built\n" + getTimeStamp() + "\n");
		else { new File(fasta.getName() + ".fai").delete(); }
		
		return properFASTA;
    }
    
	private static String getTimeStamp() {
		Date date= new Date();
		String time = new Timestamp(date.getTime()).toString();
		return time;
	}
}
