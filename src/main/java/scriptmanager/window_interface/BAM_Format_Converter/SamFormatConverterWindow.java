package scriptmanager.window_interface.BAM_Format_Converter;

import htsjdk.samtools.SAMException;
import scriptmanager.scripts.BAM_Format_Converter.SamFormatConverterWrapper;
import scriptmanager.scripts.BAM_Format_Converter.SamtoFastqWrapper;
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

public class SamFormatConverterWindow extends JFrame implements ActionListener, PropertyChangeListener {
    private JPanel contentPane;
    protected JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));

    final DefaultListModel<String> expList;

    Vector<File> BAMFiles = new Vector<File>();
    private File OUT_DIR = null;

    private JButton btnLoadBam;
    private JButton btnLoadSam;
    private JButton btnRemoveBam;
    private JButton btnOutput;
    private JLabel outputLabel;
    private JLabel lblDefaultToLocal;
    private JButton btnConvert;
    private JProgressBar progressBar;
    public SamFormatConverterWindow.Task task;

    class Task extends SwingWorker<Void, Void> {

        @Override
        public Void doInBackground() throws IOException {
            setProgress(0);
            try {
                for(int x = 0; x < BAMFiles.size(); x++) {
                    // Build output filepath
                    String[] NAME = BAMFiles.get(x).getName().split("\\.");
                    File OUTPUT = null;
                    if (BAMFiles.get(x).getName().contains("sam")) {
                        if (OUT_DIR != null) {
                            OUTPUT = new File(OUT_DIR.getCanonicalPath() + File.separator + NAME[0] + "_converted.bam");
                        } else {
                            OUTPUT = new File(NAME[0] + "_converted.bam");
                        }
                    } else {
                        if (OUT_DIR != null) {
                            OUTPUT = new File(OUT_DIR.getCanonicalPath() + File.separator + NAME[0] + "_converted.sam");
                        } else {
                            OUTPUT = new File(NAME[0] + "_converted.sam");
                        }
                    }

                    // Execute Picard wrapper
                    SamFormatConverterWrapper.run(BAMFiles.get(x), OUTPUT);
                    // Update progress
                    int percentComplete = (int)(((double)(x + 1) / BAMFiles.size()) * 100);
                    setProgress(percentComplete);
                }
                setProgress(100);
                JOptionPane.showMessageDialog(null, "Conversion Complete");
            } catch (SAMException se) {
                JOptionPane.showMessageDialog(null, se.getMessage());
            }
            return null;
        }

        public void done() {
            massXable(contentPane, true);
            setCursor(null); //turn off the wait cursor
        }
    }
    public SamFormatConverterWindow() {
        setTitle("Convert SAM to BAM or BAM to SAM");
        setBounds(125, 125, 480, 450);
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

        btnLoadBam = new JButton("Load BAM Files");
        sl_contentPane.putConstraint(SpringLayout.WEST, btnLoadBam, 5, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane, 6, SpringLayout.SOUTH, btnLoadBam);

        btnLoadBam.addActionListener(new ActionListener() {
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
        contentPane.add(btnLoadBam);

        btnRemoveBam = new JButton("Remove BAM");
        sl_contentPane.putConstraint(SpringLayout.NORTH, btnLoadBam, 0, SpringLayout.NORTH, btnRemoveBam);
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

        btnLoadSam = new JButton("Load SAM Files");
        sl_contentPane.putConstraint(SpringLayout.NORTH, btnLoadSam, 0, SpringLayout.NORTH, contentPane);
        sl_contentPane.putConstraint(SpringLayout.WEST, btnLoadSam, 5, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.NORTH, btnLoadSam, 30, SpringLayout.NORTH, btnLoadBam);
        sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane, 30, SpringLayout.NORTH, btnLoadSam);

        btnLoadSam.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                File[] newBAMFiles = FileSelection.getFiles(fc,"sam");
                if(newBAMFiles != null) {
                    for(int x = 0; x < newBAMFiles.length; x++) {
                        BAMFiles.add(newBAMFiles[x]);
                        expList.addElement(newBAMFiles[x].getName());
                    }
                }
            }
        });
        contentPane.add(btnLoadSam);

        btnOutput = new JButton("Output Directory");
        btnOutput.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                File temp = FileSelection.getOutputDir(fc);
                if(temp != null) {
                    OUT_DIR = temp;
                    lblDefaultToLocal.setText(OUT_DIR.getAbsolutePath());
                }
            }
        });
        sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane, -3, SpringLayout.NORTH, btnOutput);
        sl_contentPane.putConstraint(SpringLayout.WEST, btnOutput, 150, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, btnOutput, -50, SpringLayout.SOUTH, contentPane);
        sl_contentPane.putConstraint(SpringLayout.EAST, btnOutput, -150, SpringLayout.EAST, contentPane);
        contentPane.add(btnOutput);

        outputLabel = new JLabel("Current Output:");
        sl_contentPane.putConstraint(SpringLayout.WEST, outputLabel, 0, SpringLayout.WEST, scrollPane);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, outputLabel, 0, SpringLayout.SOUTH, contentPane);
        contentPane.add(outputLabel);

        lblDefaultToLocal = new JLabel("Default to Local Directory");
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblDefaultToLocal, 0, SpringLayout.NORTH, outputLabel);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblDefaultToLocal, 6, SpringLayout.EAST, outputLabel);
        lblDefaultToLocal.setBackground(Color.WHITE);
        contentPane.add(lblDefaultToLocal);

        btnConvert = new JButton("Convert");
        sl_contentPane.putConstraint(SpringLayout.NORTH, btnConvert, 2, SpringLayout.SOUTH, scrollPane);
        sl_contentPane.putConstraint(SpringLayout.EAST, btnConvert, -5, SpringLayout.EAST, contentPane);
        contentPane.add(btnConvert);

        progressBar = new JProgressBar();
        sl_contentPane.putConstraint(SpringLayout.NORTH, progressBar, -3, SpringLayout.NORTH, lblDefaultToLocal);
        sl_contentPane.putConstraint(SpringLayout.EAST, progressBar, 0, SpringLayout.EAST, scrollPane);
        progressBar.setStringPainted(true);
        contentPane.add(progressBar);


        btnConvert.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                task = new Task();
                task.addPropertyChangeListener(SamFormatConverterWindow.this);
                task.execute();
            }
        });
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
}
