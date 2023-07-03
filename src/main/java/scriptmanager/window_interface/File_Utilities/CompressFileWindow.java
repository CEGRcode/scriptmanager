package scriptmanager.window_interface.File_Utilities;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

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

import scriptmanager.util.FileSelection;
import scriptmanager.scripts.File_Utilities.GZipFiles;

/**
 * Graphical interface window for calling static gzip compressing method implemented in the scripts package.
 * 
 * @author Olivia Lang
 *
 */
@SuppressWarnings("serial")
public class CompressFileWindow extends JFrame implements ActionListener, PropertyChangeListener {
	private JPanel contentPane;
	protected JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));	

	final DefaultListModel<String> expList;
	Vector<File> GeneralFiles = new Vector<File>();

	private JButton btnLoad;
	private JButton btnRemoveFile;
	private JButton btnCompress;

	private JProgressBar progressBar;
	public Task task;

	/**
	 * Organizes user inputs for calling script
	 */
	class Task extends SwingWorker<Void, Void> {
		@Override
		public Void doInBackground() throws IOException {
			setProgress(0);
			for(int x = 0; x < GeneralFiles.size(); x++) {
				GZipFiles.compressFile(GeneralFiles.get(x), 8192);
				int percentComplete = (int)(((double)(x + 1) / GeneralFiles.size()) * 100);
				setProgress(percentComplete);
			}
			setProgress(100);
			JOptionPane.showMessageDialog(null, "Compressing Complete");
			return null;
		}

		public void done() {
			massXable(contentPane, true);
			setCursor(null); //turn off the wait cursor
		}
	}

	/**
	 * Instantiate window with graphical interface design.
	 */
	public CompressFileWindow() {
		setTitle("Compress (GZip) Files");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setBounds(125, 125, 500, 260);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);

		JScrollPane scrollPane = new JScrollPane();
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane, 5, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollPane, -5, SpringLayout.EAST, contentPane);
		contentPane.add(scrollPane);

		expList = new DefaultListModel<String>();
		final JList<String> listExp = new JList<String>(expList);
		listExp.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		scrollPane.setViewportView(listExp);

		btnLoad = new JButton("Load Files");
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane, 6, SpringLayout.SOUTH, btnLoad);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnLoad, 0, SpringLayout.WEST, scrollPane);
		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File[] newFiles = FileSelection.getGenericFiles(fc);
				if(newFiles != null) {
					for(int x = 0; x < newFiles.length; x++) { 
						GeneralFiles.add(newFiles[x]);
						expList.addElement(newFiles[x].getName());
					}
				}
			}
		});
		contentPane.add(btnLoad);

		btnRemoveFile = new JButton("Remove File");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnLoad, 0, SpringLayout.NORTH, btnRemoveFile);
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnRemoveFile, 0, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnRemoveFile, -5, SpringLayout.EAST, contentPane);
		btnRemoveFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				while(listExp.getSelectedIndex() > -1) {
					GeneralFiles.remove(listExp.getSelectedIndex());
					expList.remove(listExp.getSelectedIndex());
				}
			}
		});		
		contentPane.add(btnRemoveFile);

		btnCompress = new JButton("Compress");
		sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane, -10, SpringLayout.NORTH, btnCompress);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnCompress, 167, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnCompress, 0, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnCompress, -175, SpringLayout.EAST, contentPane);
		contentPane.add(btnCompress);

		progressBar = new JProgressBar();
		sl_contentPane.putConstraint(SpringLayout.NORTH, progressBar, 3, SpringLayout.NORTH, btnCompress);
		sl_contentPane.putConstraint(SpringLayout.EAST, progressBar, 0, SpringLayout.EAST, scrollPane);
		progressBar.setStringPainted(true);
		contentPane.add(progressBar);

		btnCompress.setActionCommand("start");
		btnCompress.addActionListener(this);
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
	 * Invoked when task's progress property changes.
	 */
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
		for(Component c : con.getComponents()) {
			c.setEnabled(status);
			if(c instanceof Container) { massXable((Container)c, status); }
		}
	}
}
