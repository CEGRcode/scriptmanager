package scriptmanager.window_interface.BAM_Manipulation;

import htsjdk.samtools.SAMException;
import scriptmanager.scripts.BAM_Manipulation.DownsampleSamFile;
import scriptmanager.util.FileSelection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

/**
 * @author Erik Pavloski
 * This is the window class for DownsampleSam
 */
public class DownsampleSamFileWindow extends JFrame implements ActionListener, PropertyChangeListener {
    private JPanel contentPane;
    protected JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));

    final DefaultListModel<String> expList;

    Vector<File> BAMFiles = new Vector<File>();
    private JButton btnLoad;
    private JButton btnRemoveBam;
    private JButton btnDownSample;

    private JProgressBar progressBar;
    public Task task;

    class Task extends SwingWorker<Void, Void> {
        double probability;

        public Task (double probability) {
            this.probability = probability;
        }

        @Override
        public Void doInBackground() throws IOException {
            setProgress(0);
            try {
                for (int x = 0; x < BAMFiles.size(); x++) {
                    // Execute picard wrapper
                    DownsampleSamFile.run(BAMFiles.get(x), probability);
                    // Update Progress
                    int percentComplete = (int) (((double) (x + 1) / BAMFiles.size()) * 100);
                    setProgress(percentComplete);

                }
                setProgress(100);
                JOptionPane.showMessageDialog(null, "Down-sampling Complete");
                return null;
            } catch (SAMException sme) {
                JOptionPane.showMessageDialog(null, sme.getMessage());
                return null;
            }
        }

        public void done() {
            massXable(contentPane, true);
            setCursor(null);
        }
    }

    public DownsampleSamFileWindow() {
        setTitle("Downsample SAM/BAM File");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setBounds(125, 125, 450, 360);
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

        btnLoad = new JButton("Load BAM Files");
        sl_contentPane.putConstraint(SpringLayout.WEST, btnLoad, 5, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane, 6, SpringLayout.SOUTH, btnLoad);

        btnLoad.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                File[] newBAMFiles = FileSelection.getFiles(fc,"bam");
                if(newBAMFiles != null) {
                    for(int x = 0; x < newBAMFiles.length; x++) {
                        BAMFiles.add(newBAMFiles[x]);
                        expList.addElement(newBAMFiles[x].getName());
                    }
                }
            }
        });
        contentPane.add(btnLoad);

        btnRemoveBam = new JButton("Remove BAM");
        sl_contentPane.putConstraint(SpringLayout.NORTH, btnLoad, 0, SpringLayout.NORTH, btnRemoveBam);
        sl_contentPane.putConstraint(SpringLayout.NORTH, btnRemoveBam, 0, SpringLayout.NORTH, contentPane);
        sl_contentPane.putConstraint(SpringLayout.EAST, btnRemoveBam, -5, SpringLayout.EAST, contentPane);
        btnRemoveBam.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                while(listExp.getSelectedIndex() > -1) {
                    BAMFiles.remove(listExp.getSelectedIndex());
                    expList.remove(listExp.getSelectedIndex());
                }
            }
        });
        contentPane.add(btnRemoveBam);


        JLabel probabilityLabel = new JLabel("Probability: ");
        sl_contentPane.putConstraint(SpringLayout.NORTH, probabilityLabel, 6, SpringLayout.SOUTH, scrollPane);
        sl_contentPane.putConstraint(SpringLayout.WEST, probabilityLabel, 0, SpringLayout.WEST, scrollPane);
        contentPane.add(probabilityLabel);

        JTextField probabilityField = new JTextField("0.5");
        probabilityField.setPreferredSize(new Dimension(50, probabilityField.getPreferredSize().height));
        sl_contentPane.putConstraint(SpringLayout.EAST, probabilityField, 50, SpringLayout.EAST, probabilityLabel);
        sl_contentPane.putConstraint(SpringLayout.NORTH, probabilityField, 203, SpringLayout.NORTH, contentPane);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane, -4, SpringLayout.NORTH, probabilityField);
        contentPane.add(probabilityField);

        btnDownSample = new JButton("Down-sample");
        sl_contentPane.putConstraint(SpringLayout.WEST, btnDownSample, 5, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, btnDownSample, -10, SpringLayout.SOUTH, contentPane);
        btnDownSample.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               massXable(contentPane, false);
               setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

               double probability = Double.parseDouble(probabilityField.getText());
               task = new Task(probability);
               task.addPropertyChangeListener(DownsampleSamFileWindow.this);
               task.execute();
            }
        });
        contentPane.add(btnDownSample);

        progressBar = new JProgressBar();
        sl_contentPane.putConstraint(SpringLayout.NORTH, progressBar, 3, SpringLayout.NORTH, btnDownSample);
        sl_contentPane.putConstraint(SpringLayout.EAST, progressBar, 0, SpringLayout.EAST, scrollPane);
        progressBar.setStringPainted(true);
        contentPane.add(progressBar);
    }
    @Override
    public void actionPerformed(ActionEvent arg0) {
        massXable(contentPane, false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        task = new DownsampleSamFileWindow().task;
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

}
