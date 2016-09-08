package util;

import java.io.File;

import javax.swing.JFileChooser;

public class FileSelection {

	public static File getGenericFile(JFileChooser fc, boolean enable){
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(enable);
		fc.setSelectedFile(new File(""));
		fc.setDialogTitle("File Selection");
		File Files = null;
		int returnVal = fc.showOpenDialog(fc);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			Files = fc.getSelectedFile();
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
	
	public static File[] getBAMFiles(JFileChooser fc){
		fc.setFileFilter(new ExtensionFileFilter("bam"));
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(true);
		fc.setSelectedFile(new File(""));
		fc.setDialogTitle("BAM File Selection");
		File[] bamFiles = null;
		int returnVal = fc.showOpenDialog(fc);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			bamFiles = fc.getSelectedFiles();
		}
		return bamFiles;
	}
	
	public static File[] getBEDFiles(JFileChooser fc){
		fc.setFileFilter(new ExtensionFileFilter("bed"));
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(true);
		fc.setSelectedFile(new File(""));
		fc.setDialogTitle("BED File Selection");
		File[] bamFiles = null;
		int returnVal = fc.showOpenDialog(fc);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			bamFiles = fc.getSelectedFiles();
		}
		return bamFiles;
	}
	
	public static File getBEDFile(JFileChooser fc) {
		fc.setFileFilter(new ExtensionFileFilter("bed"));
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(false);
		fc.setSelectedFile(new File(""));
		fc.setDialogTitle("BED File Selection");
		File bedFile = null;
		int returnVal = fc.showOpenDialog(fc);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			bedFile = fc.getSelectedFile();
		}
		return bedFile;
	}
	
	public static File[] getCDTFiles(JFileChooser fc) {
		fc.setFileFilter(new ExtensionFileFilter("cdt"));
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(true);
		fc.setSelectedFile(new File(""));
		fc.setDialogTitle("CDT File Selection");
		File[] cdtFiles = null;
		int returnVal = fc.showOpenDialog(fc);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			cdtFiles = fc.getSelectedFiles();
		}
		return cdtFiles;
	}
	
	public static File getCDTFile(JFileChooser fc) {
		fc.setFileFilter(new ExtensionFileFilter("cdt"));
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(false);
		fc.setSelectedFile(new File(""));
		fc.setDialogTitle("CDT File Selection");
		File cdtFile = null;
		int returnVal = fc.showOpenDialog(fc);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			cdtFile = fc.getSelectedFile();
		}
		return cdtFile;
	}
		
	public static File[] getFASTAFiles(JFileChooser fc){
		fc.setFileFilter(new ExtensionFileFilter("fa"));
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(true);
		fc.setSelectedFile(new File(""));
		fc.setDialogTitle("FASTA File Selection");
		File[] fastaFiles = null;
		int returnVal = fc.showOpenDialog(fc);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			fastaFiles = fc.getSelectedFiles();
		}
		return fastaFiles;
	}
	
	public static File getFASTAFile(JFileChooser fc){
		fc.setFileFilter(new ExtensionFileFilter("fa"));
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(false);
		fc.setSelectedFile(new File(""));
		fc.setDialogTitle("FASTA File Selection");
		File fastaFile = null;
		int returnVal = fc.showOpenDialog(fc);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			fastaFile = fc.getSelectedFile();
		}
		return fastaFile;
	}
	
	public static File[] getGFFFiles(JFileChooser fc){
		fc.setFileFilter(new ExtensionFileFilter("gff"));
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(true);
		fc.setSelectedFile(new File(""));
		fc.setDialogTitle("GFF File Selection");
		File[] gffFiles = null;
		int returnVal = fc.showOpenDialog(fc);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			gffFiles = fc.getSelectedFiles();
		}
		return gffFiles;
	}
	
	public static File getGFFFile(JFileChooser fc) {
		fc.setFileFilter(new ExtensionFileFilter("gff"));
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(false);
		fc.setSelectedFile(new File(""));
		fc.setDialogTitle("GFF File Selection");
		File gffFile = null;
		int returnVal = fc.showOpenDialog(fc);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			gffFile = fc.getSelectedFile();
		}
		return gffFile;
	}
	
	public static File[] getPNGFiles(JFileChooser fc){
		fc.setFileFilter(new ExtensionFileFilter("png"));
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(true);
		fc.setSelectedFile(new File(""));
		fc.setDialogTitle("PNG File Selection");
		File[] gffFiles = null;
		int returnVal = fc.showOpenDialog(fc);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			gffFiles = fc.getSelectedFiles();
		}
		return gffFiles;
	}
}
