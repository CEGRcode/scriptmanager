package window_interface.Sequence_Analysis;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import objects.CustomOutputStream;
import scripts.Sequence_Analysis.SearchMotif;

@SuppressWarnings("serial")
public class SearchMotifOutput extends JFrame{
	
	private int ALLOWED_MISMATCH;
	private String motif;
	private File INPUTFILE = null;
	private File OUT;
	
	private JTextArea textArea;
	
	public SearchMotifOutput(File input, String mot, int num, File output) throws IOException {
		setTitle("Motif Search Progress");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(150, 150, 600, 800);
	
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
	
		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
	
		ALLOWED_MISMATCH = num;
		motif = mot;
		INPUTFILE = input;
		OUT = output;
	}

	public void run() throws IOException, InterruptedException {
		
		PrintStream PS = new PrintStream(new CustomOutputStream(textArea));
		SearchMotif script_obj = new SearchMotif(INPUTFILE, motif, ALLOWED_MISMATCH, OUT, PS);
		script_obj.run();
		
		Thread.sleep(2000);
		dispose();
	}
}