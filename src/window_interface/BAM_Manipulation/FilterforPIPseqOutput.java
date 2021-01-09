package window_interface.BAM_Manipulation;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import objects.CustomOutputStream;
import objects.CustomExceptions.FASTAException;
import scripts.BAM_Manipulation.FilterforPIPseq;

@SuppressWarnings("serial")
public class FilterforPIPseqOutput extends JFrame {
	File bamFile = null;
	File genome = null;
	File output = null;
	String SEQ = "";
	
	private JTextArea textArea;
	
	public FilterforPIPseqOutput(File in, File gen, File out, String s) {
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
	
	public void run() throws IOException, InterruptedException, FASTAException {
		
		PrintStream PS = new PrintStream(new CustomOutputStream(textArea));	

		//Check if BAI index file exists
		File f = new File(bamFile + ".bai");
		if(f.exists() && !f.isDirectory()) {
			
			FilterforPIPseq script_obj = new FilterforPIPseq(bamFile, genome, output, SEQ, PS);
			script_obj.run();
			
			Thread.sleep(2000);
			dispose();
		} else {
			JOptionPane.showMessageDialog(null, "BAI Index File does not exist for: " + bamFile.getName() + "\n");
		}
	}
}
