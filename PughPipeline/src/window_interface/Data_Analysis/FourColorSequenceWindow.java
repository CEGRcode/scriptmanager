package window_interface.Data_Analysis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.SwingWorker;
import javax.swing.JProgressBar;
import javax.swing.JLabel;

import java.awt.Font;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import util.FileSelection;

@SuppressWarnings("serial")
public class FourColorSequenceWindow extends JFrame implements ActionListener, PropertyChangeListener {
	private JPanel contentPane;
	protected JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));	
	
	final DefaultListModel expList;
	Vector<File> fastaFiles = new Vector<File>();
	private File OUTPUTPATH = null;
	
	private JButton btnLoad;
	private JButton btnRemoveBam;
	private JButton btnGen;

	private JProgressBar progressBar;
	public Task task;
	private JLabel lblCurrentOutput;
	private JLabel lblDefaultToLocal;
	private JButton btnOutputDirectory;
	
	class Task extends SwingWorker<Void, Void> {
        @Override
        public Void doInBackground() throws IOException {
        	setProgress(0);
        	if(OUTPUTPATH == null) {
        		OUTPUTPATH = new File(System.getProperty("user.dir"));
        	}
        	for(int x = 0; x < fastaFiles.size(); x++) {
        		String[] out = fastaFiles.get(x).getName().split("\\.");
				generatePLOT(fastaFiles.get(x), new File(OUTPUTPATH + File.separator + out[0] + ".png"));
				int percentComplete = (int)(((double)(x + 1) / fastaFiles.size()) * 100);
        		setProgress(percentComplete);
        	}
        	setProgress(100);
			JOptionPane.showMessageDialog(null, "Plots Generated");
        	return null;
        }
        
        public void done() {
        	massXable(contentPane, true);
            setCursor(null); //turn off the wait cursor
        }
	}
	
	public FourColorSequenceWindow() {
		setTitle("Four Color Sequence Plot Generator");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setBounds(125, 125, 450, 320);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);
	
		JScrollPane scrollPane = new JScrollPane();
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane, 5, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollPane, -5, SpringLayout.EAST, contentPane);
		contentPane.add(scrollPane);
		
      	expList = new DefaultListModel();
		final JList listExp = new JList(expList);
		listExp.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		scrollPane.setViewportView(listExp);
		
		btnLoad = new JButton("Load FASTA Files");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnLoad, 5, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane, 6, SpringLayout.SOUTH, btnLoad);
		btnLoad.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				File[] newFASTAFiles = FileSelection.getFASTAFiles(fc);
				if(newFASTAFiles != null) {
					for(int x = 0; x < newFASTAFiles.length; x++) { 
						fastaFiles.add(newFASTAFiles[x]);
						expList.addElement(newFASTAFiles[x].getName());
					}
				}
			}
		});
		contentPane.add(btnLoad);
		
		btnRemoveBam = new JButton("Remove FASTA Files");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnLoad, 0, SpringLayout.NORTH, btnRemoveBam);
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnRemoveBam, 0, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnRemoveBam, -5, SpringLayout.EAST, contentPane);
		btnRemoveBam.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				while(listExp.getSelectedIndex() > -1) {
					fastaFiles.remove(listExp.getSelectedIndex());
					expList.remove(listExp.getSelectedIndex());
				}
			}
		});		
		contentPane.add(btnRemoveBam);
		
		btnGen = new JButton("Generate");
		sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane, -92, SpringLayout.NORTH, btnGen);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnGen, 167, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnGen, 0, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnGen, -175, SpringLayout.EAST, contentPane);
		contentPane.add(btnGen);
		
		progressBar = new JProgressBar();
		sl_contentPane.putConstraint(SpringLayout.NORTH, progressBar, 3, SpringLayout.NORTH, btnGen);
		sl_contentPane.putConstraint(SpringLayout.EAST, progressBar, -5, SpringLayout.EAST, contentPane);
        progressBar.setStringPainted(true);
		contentPane.add(progressBar);
		
        btnGen.setActionCommand("start");
        
        lblCurrentOutput = new JLabel("Current Output:");
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblCurrentOutput, 39, SpringLayout.SOUTH, scrollPane);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblCurrentOutput, 0, SpringLayout.WEST, scrollPane);
        lblCurrentOutput.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        contentPane.add(lblCurrentOutput);
        
        lblDefaultToLocal = new JLabel("Default to Local Directory");
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblDefaultToLocal, 10, SpringLayout.SOUTH, lblCurrentOutput);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblDefaultToLocal, 10, SpringLayout.WEST, lblCurrentOutput);
        sl_contentPane.putConstraint(SpringLayout.EAST, lblDefaultToLocal, 313, SpringLayout.EAST, lblCurrentOutput);
        lblDefaultToLocal.setFont(new Font("Dialog", Font.PLAIN, 12));
        lblDefaultToLocal.setBackground(Color.WHITE);
        contentPane.add(lblDefaultToLocal);
        
        btnOutputDirectory = new JButton("Output Directory");
        sl_contentPane.putConstraint(SpringLayout.NORTH, btnOutputDirectory, 6, SpringLayout.SOUTH, scrollPane);
        sl_contentPane.putConstraint(SpringLayout.WEST, btnOutputDirectory, 145, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.EAST, btnOutputDirectory, -145, SpringLayout.EAST, contentPane);
        contentPane.add(btnOutputDirectory);
        
        btnOutputDirectory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	OUTPUTPATH = FileSelection.getOutputDir(fc);
				if(OUTPUTPATH != null) {
					lblDefaultToLocal.setText(OUTPUTPATH.getAbsolutePath());
				}
			}
		});
        
        btnGen.addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		massXable(contentPane, false);
    	setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        task = new Task();
        task.addPropertyChangeListener(this);
        task.execute();
	}
	
	/**
     * Invoked when task's progress property changes.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
        }
    }
	
	public void massXable(Container con, boolean status) {
		for(Component c : con.getComponents()) {
			c.setEnabled(status);
			if(c instanceof Container) { massXable((Container)c, status); }
		}
	}
    
	/**
	 * Visualize sequences as color pixels
	 * @param width, width of each base, in pixel
	 * @param height, height of each base, in pixel
	 */
	public static void generatePLOT(File input, File output) throws IOException {
		int width = 3;
		int height = 1;
		
		List<String> seq = new ArrayList<String>();
		int maxLen = 0;

		Scanner scan = new Scanner(input);
		while (scan.hasNextLine()) {
			String temp = scan.nextLine();
			if(!temp.contains(">")) {
				if (maxLen < temp.length()) maxLen = temp.length();
				seq.add(temp);
			}
		}
		scan.close();
		int pixwidth = maxLen * width;
		int pixheight = seq.size() * height;
		
		System.setProperty("java.awt.headless", "true");
		BufferedImage im = new BufferedImage(pixwidth, pixheight,BufferedImage.TYPE_INT_ARGB);
        Graphics g = im.getGraphics();
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(Color.WHITE);
        g2.fillRect(0,0,pixwidth, pixheight);
        
        int count = 0;
        for (int x = 0; x < seq.size(); x++){
        	String s = seq.get(x);
        	char[] letters = s.toCharArray();
        	for (int j=0;j<letters.length;j++){
        		switch(letters[j]){
        		case 'A':
        		case 'a':
        			g.setColor(new Color(254, 25, 24));
        			break;
        		case 'C':
        		case 'c':
                    g.setColor(new Color(43, 49, 246));
        			break;
        		case 'G':
        		case 'g':
                    g.setColor(new Color(252, 252, 80));
        			break;
        		case 'T':
        		case 't':
                    g.setColor(new Color(50, 204, 60));
        			break;
        		case '-':
                    g.setColor(Color.WHITE);
        			break;
                default:
                	g.setColor(Color.GRAY);
        		}
                g.fillRect(j*width, count*height, width, height);
        	}
            count++;
        }
        try {
            ImageIO.write(im, "png", output);
        }  catch (IOException ex) {
            ex.printStackTrace();
        }
	}
}


	
