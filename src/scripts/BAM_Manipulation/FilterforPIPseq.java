package scripts.BAM_Manipulation;

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
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import util.FASTAUtilities;

@SuppressWarnings("serial")
public class FilterforPIPseq extends JFrame {
	File bamFile = null;
	File genome = null;
	File output = null;
	String SEQ = "";
	
	boolean FASTA_INDEX = true;
	
	private JTextArea textArea;
	
	public FilterforPIPseq(File in, File gen, File out, String s) {
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
	
	public void run() throws IOException, InterruptedException {
		File FAI = new File(genome + ".fai");
		//Check if FAI index file exists
		if(!FAI.exists() || FAI.isDirectory()) {
			textArea.append("FASTA Index file not found.\nGenerating new one...\n");
			FASTA_INDEX = FASTAUtilities.buildFASTAIndex(genome);
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
			
			Thread.sleep(2000);
			dispose();
		} else {
			JOptionPane.showMessageDialog(null, "BAI Index File does not exist for: " + bamFile.getName() + "\n");
		}
	}
}
