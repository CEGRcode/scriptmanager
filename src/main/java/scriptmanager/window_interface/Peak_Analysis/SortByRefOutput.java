package scriptmanager.window_interface.Peak_Analysis;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import scriptmanager.objects.CustomOutputStream;
import scriptmanager.scripts.Peak_Analysis.SortByRef;
import scriptmanager.util.ExtensionFileFilter;

@SuppressWarnings("serial")
public class SortByRefOutput extends JFrame{
	private File PEAK = null;
	private File REF = null;
	private File OUTFILE = null;
	private Boolean PROPER_STRAND = false;
	private Boolean GZIP_OUTPUT = false;
	private Boolean GFF = false;

	private JTextArea textArea;
		
	public SortByRefOutput(File ref, File peak, File outpath, boolean properStrands, boolean gzOutput, boolean gff) throws IOException {
		setTitle("BED Align to Reference Progress");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(150, 150, 600, 800);
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
		
		REF = ref;
		PEAK = peak;
		GFF = gff;
		PROPER_STRAND = properStrands;
		GZIP_OUTPUT = gzOutput;
		
		if(outpath != null) {
			OUTFILE = new File(outpath.getCanonicalPath() + File.separator + PEAK.getName().split("\\.")[0] + "_" + 
			REF.getName().split("\\.")[0] + "_Output." + ExtensionFileFilter.getExtensionIgnoreGZ(REF));
		} else {
			OUTFILE = new File(PEAK.getName().split("\\.")[0] + "_" + REF.getName().split("\\.")[0] + 
			"_Output." + ExtensionFileFilter.getExtensionIgnoreGZ(REF));
		}
	}
		
	public void run() throws IOException, InterruptedException {
		PrintStream PS = new PrintStream(new CustomOutputStream(textArea));
		SortByRef script_obj = new SortByRef(REF, PEAK, OUTFILE, PROPER_STRAND, GZIP_OUTPUT, PS);
		if (GFF) {	
			script_obj.sortGFF(); 
		} else {
			script_obj.sortBED(); 
		}

		Thread.sleep(2000);
		dispose();
	}
}