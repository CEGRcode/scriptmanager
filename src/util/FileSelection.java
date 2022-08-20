package util;

import java.io.File;

import javax.swing.JFileChooser;

public class FileSelection {
	
	public static File[] getGenericFiles(JFileChooser fc){
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(true);
		fc.setSelectedFile(new File(""));
		fc.setDialogTitle("File Selection");
		File[] Files = null;
		int returnVal = fc.showOpenDialog(fc);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			Files = fc.getSelectedFiles();
		}
		return Files;
	}
	
	public static File getOutputDir(JFileChooser fc) {
		fc.setDialogTitle("Output Directory");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setAcceptAllFileFilterUsed(false);
		if (fc.showOpenDialog(fc) == JFileChooser.APPROVE_OPTION) { 
			return fc.getSelectedFile();
		} else {
			return null;
		}	
	}
	
	public static File getFile(JFileChooser fc, String ext){
		fc.setFileFilter(new ExtensionFileFilter(ext));
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(false);
		fc.setSelectedFile(new File(""));
		fc.setDialogTitle("File Selection");
		File Files = null;
		int returnVal = fc.showOpenDialog(fc);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			Files = fc.getSelectedFile();
		}
		return Files;
	}
	
	public static File[] getFiles(JFileChooser fc, String ext){
		return getFiles(fc, ext, false);
	}

	public static File[] getFiles(JFileChooser fc, String ext, boolean includeGZ){
		fc.setFileFilter(new ExtensionFileFilter(ext,includeGZ));
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(true);
		fc.setSelectedFile(new File(""));
		fc.setDialogTitle("File Selection");
		File[] Files = null;
		int returnVal = fc.showOpenDialog(fc);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			Files = fc.getSelectedFiles();
		}
		return Files;
	}
}