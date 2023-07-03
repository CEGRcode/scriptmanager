package scriptmanager.window_interface.BAM_Manipulation;

import java.awt.Component;
import java.awt.Container;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import scriptmanager.util.FileSelection;
import scriptmanager.scripts.BAM_Manipulation.BAIIndexer;

/**
 * Graphical window for user argument selection and execution of the FilterforPIPseq script
 * @see scriptmanager.scripts.BAM_Manipulation.FilterforPIPseq
 * @see scriptmanager.window_interface.BAM_Manipulation.FilterforPIPseqWindow
 */
@SuppressWarnings("serial")
public class FilterforPIPseqWindow extends JFrame implements ActionListener, PropertyChangeListener {
	private JPanel contentPane;
	protected JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));

	final DefaultListModel<String> expList;
	private File OUTPUT_PATH = null;
	private File GENOME = null;
	List<File> BAMFiles = new ArrayList<File>();

	private JButton btnLoad;
	private JButton btnRemoveBam;
	private JButton btnFilter;

	private JProgressBar progressBar;
	public Task task;
	private JButton btnOutput;
	private JLabel lblCurrent;
	private JLabel lblDefaultToLocal;
	private JCheckBox chckbxGenerateBaiIndex;
	private JButton btnLoadGenome;
	private JLabel lblReferenceGenome;
	private JLabel lblFilterByUpstream;
	private JTextField txtSeq;

	/**
	 * Organizes user inputs for calling script
	 */
	class Task extends SwingWorker<Void, Void> {
		@Override
		public Void doInBackground() throws Exception {
			setProgress(0);
			try {
				for (int x = 0; x < BAMFiles.size(); x++) {
					String[] NAME = BAMFiles.get(x).getName().split("\\.");
					File OUTPUT = null;
					if (OUTPUT_PATH != null) {
						OUTPUT = new File(OUTPUT_PATH.getCanonicalPath() + File.separator + NAME[0] + "_PSfilter.bam");
					} else {
						OUTPUT = new File(NAME[0] + "_PSfilter.bam");
					}
					FilterforPIPseqOutput filter = new FilterforPIPseqOutput(BAMFiles.get(x), GENOME, OUTPUT,
							txtSeq.getText());
					filter.setVisible(true);
					filter.run();

					if (chckbxGenerateBaiIndex.isSelected()) {
						BAIIndexer.generateIndex(OUTPUT);
					}

					int percentComplete = (int) (((double) (x + 1) / BAMFiles.size()) * 100);
					setProgress(percentComplete);
				}
				setProgress(100);
				JOptionPane.showMessageDialog(null, "Permanganate-Seq Filtering Complete");
			} catch (IOException ioe) {
				ioe.printStackTrace();
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
			return null;
		}

		public void done() {
			massXable(contentPane, true);
			setCursor(null); // turn off the wait cursor
		}
	}

	/**
	 * Creates a new FilterforPIPseqWindow
	 */
	public FilterforPIPseqWindow() {
		setTitle("Filter PIP-seq Reads");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setBounds(125, 125, 450, 375);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);

		JScrollPane scrollPane = new JScrollPane();
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane, 71, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane, 5, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollPane, -5, SpringLayout.EAST, contentPane);
		contentPane.add(scrollPane);

		expList = new DefaultListModel<String>();
		final JList<String> listExp = new JList<String>(expList);
		listExp.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		scrollPane.setViewportView(listExp);

		btnLoad = new JButton("Load BAM Files");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnLoad, 0, SpringLayout.WEST, scrollPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnLoad, -6, SpringLayout.NORTH, scrollPane);
		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File[] newBAMFiles = FileSelection.getFiles(fc, "bam");
				if (newBAMFiles != null) {
					for (int x = 0; x < newBAMFiles.length; x++) {
						BAMFiles.add(newBAMFiles[x]);
						expList.addElement(newBAMFiles[x].getName());
					}
				}
			}
		});
		contentPane.add(btnLoad);

		btnRemoveBam = new JButton("Remove BAM");
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnRemoveBam, -6, SpringLayout.NORTH, scrollPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnRemoveBam, 0, SpringLayout.EAST, scrollPane);
		btnRemoveBam.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				while (listExp.getSelectedIndex() > -1) {
					BAMFiles.remove(listExp.getSelectedIndex());
					expList.remove(listExp.getSelectedIndex());
				}
			}
		});
		contentPane.add(btnRemoveBam);

		btnFilter = new JButton("Filter");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnFilter, 160, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnFilter, 0, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnFilter, -160, SpringLayout.EAST, contentPane);
		contentPane.add(btnFilter);

		btnFilter.setActionCommand("start");
		btnFilter.addActionListener(this);

		progressBar = new JProgressBar();
		sl_contentPane.putConstraint(SpringLayout.NORTH, progressBar, 3, SpringLayout.NORTH, btnFilter);
		sl_contentPane.putConstraint(SpringLayout.WEST, progressBar, 40, SpringLayout.EAST, btnFilter);
		sl_contentPane.putConstraint(SpringLayout.EAST, progressBar, -10, SpringLayout.EAST, contentPane);
		progressBar.setStringPainted(true);
		contentPane.add(progressBar);

		btnOutput = new JButton("Output Directory");
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnOutput, -55, SpringLayout.SOUTH, contentPane);
		btnOutput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OUTPUT_PATH = FileSelection.getOutputDir(fc);
				if (OUTPUT_PATH != null) {
					lblDefaultToLocal.setText(OUTPUT_PATH.getAbsolutePath());
				}
			}
		});
		sl_contentPane.putConstraint(SpringLayout.WEST, btnOutput, 146, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnOutput, -146, SpringLayout.EAST, contentPane);
		contentPane.add(btnOutput);

		lblCurrent = new JLabel("Current Output:");
		sl_contentPane.putConstraint(SpringLayout.WEST, lblCurrent, 5, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lblCurrent, -30, SpringLayout.SOUTH, contentPane);
		lblCurrent.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		contentPane.add(lblCurrent);

		lblDefaultToLocal = new JLabel("Default to Local Directory");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblDefaultToLocal, 1, SpringLayout.NORTH, lblCurrent);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblDefaultToLocal, 6, SpringLayout.EAST, lblCurrent);
		lblDefaultToLocal.setBackground(Color.WHITE);
		contentPane.add(lblDefaultToLocal);

		chckbxGenerateBaiIndex = new JCheckBox("Generate BAI Index for new BAM file");
		sl_contentPane.putConstraint(SpringLayout.WEST, chckbxGenerateBaiIndex, 0, SpringLayout.WEST, scrollPane);
		chckbxGenerateBaiIndex.setSelected(true);
		contentPane.add(chckbxGenerateBaiIndex);

		btnLoadGenome = new JButton("Load Genome");
		btnLoadGenome.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File temp = FileSelection.getFile(fc, "fa");
				if (temp != null) {
					GENOME = temp;
					lblReferenceGenome.setText(GENOME.getName());
				}
			}
		});
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnLoadGenome, 0, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnLoadGenome, 5, SpringLayout.WEST, contentPane);
		contentPane.add(btnLoadGenome);

		lblReferenceGenome = new JLabel("Reference Genome");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblReferenceGenome, 5, SpringLayout.NORTH, btnLoadGenome);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblReferenceGenome, 6, SpringLayout.EAST, btnLoadGenome);
		contentPane.add(lblReferenceGenome);

		lblFilterByUpstream = new JLabel("Filter by Upstream Sequence:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblFilterByUpstream, 6, SpringLayout.SOUTH, scrollPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblFilterByUpstream, 0, SpringLayout.WEST, scrollPane);
		contentPane.add(lblFilterByUpstream);

		txtSeq = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.EAST, txtSeq, 150, SpringLayout.EAST, lblCurrent);
		sl_contentPane.putConstraint(SpringLayout.NORTH, chckbxGenerateBaiIndex, 10, SpringLayout.SOUTH, txtSeq);
		sl_contentPane.putConstraint(SpringLayout.NORTH, txtSeq, 203, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, txtSeq, 16, SpringLayout.EAST, lblFilterByUpstream);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane, -4, SpringLayout.NORTH, txtSeq);
		txtSeq.setHorizontalAlignment(SwingConstants.CENTER);
		txtSeq.setText("T");
		contentPane.add(txtSeq);
		txtSeq.setColumns(10);
	}

/**
	 * Runs when a task is invoked, making window non-interactive and executing the task.
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		massXable(contentPane, false);
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		task = new Task();
		task.addPropertyChangeListener(this);
		task.execute();
	}

	/**
	 * Invoked when task's progress changes, updating the progress bar.
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress" == evt.getPropertyName()) {
			int progress = (Integer) evt.getNewValue();
			progressBar.setValue(progress);
		}
	}

	/**
	 * Makes the content pane non-interactive If the window should be interactive data
	 * @param con Content pane to make non-interactive
	 * @param status If the window should be interactive
	 */
	public void massXable(Container con, boolean status) {
		for (Component c : con.getComponents()) {
			c.setEnabled(status);
			if (c instanceof Container) {
				massXable((Container) c, status);
			}
		}
	}

}