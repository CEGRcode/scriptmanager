package scriptmanager.window_interface.Peak_Analysis;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import scriptmanager.objects.CustomOutputStream;
import scriptmanager.scripts.Peak_Analysis.FilterBEDbyProximity;
import scriptmanager.util.ExtensionFileFilter;

@SuppressWarnings({"serial"})
public class FilterBEDbyProximityOutput extends JFrame{
	
	private int CUTOFF;
	private File INPUT;
	private String OUTBASE = null;
	
	private JTextArea textArea;
	
	public FilterBEDbyProximityOutput(File input, int cutoff, File output) throws IOException {
		setTitle("BED File Filter Progress");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(150, 150, 600, 800);
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
		
		CUTOFF = cutoff;
		INPUT = input;
		
		String INPUTNAME = ExtensionFileFilter.stripExtensionIgnoreGZ(input);
		if(output!=null){
			OUTBASE = output.getCanonicalPath() + File.separator + INPUTNAME + "_" + Integer.toString(CUTOFF) + "bp";
		}else{
			OUTBASE = INPUTNAME + "_" + Integer.toString(CUTOFF) + "bp";
		}
	}
	
	public void run() throws IOException, InterruptedException
	{
		PrintStream PS = new PrintStream(new CustomOutputStream(textArea));
		
		FilterBEDbyProximity script_obj = new FilterBEDbyProximity(INPUT, CUTOFF, OUTBASE, PS);
		script_obj.run();
		
		Thread.sleep(1000);
		dispose();
	}
}