package util;

import java.io.File;
import java.io.IOException;

import javax.swing.filechooser.FileFilter;

/**
 * Filters files such as BAI, BAM, BED, CDT, FASTA, FA, GFF, TAB for the FileSelection tool base on the extension of the filename. Also includes file extension parsing utilities used across all ScriptManager classes (especially for creating default filenames).
 * 
 * @author William KM Lai
 * @see util.FileSelection
 *
 */
public class ExtensionFileFilter extends FileFilter{

	private String ext = "";
	private String ext2 = "";
	private String ext3 = "";
	private boolean includeGZ = false;

	/**
	 * Create file filter for the given file extension. 
	 * If ExtensionFileFilter is instantiated with "fa", file extensions "fasta" and "fsa" are set to be equivalent.
	 * If ExtensionFileFilter is instantiated with "gff", file extensions "gtf" and "gff3" are set to be equivalent.
	 * 
	 * @param filter file extension to filter by (see notes on "fa" and "gff").
	 */
	public ExtensionFileFilter(String filter) {
		ext = filter;
		if(ext.equals("fa")) { ext2 = "fasta"; ext3 = "fsa"; }
		if(ext.equals("gff")) { ext2 = "gtf"; ext3 = "gff3"; }
	}

	/**
	 * Create file filter for the given file extension with option to ignore or include ".gz" extensions. 
	 * If ExtensionFileFilter is instantiated with "fa", file extensions "fasta" and "fsa" are set to be equivalent.
	 * If ExtensionFileFilter is instantiated with "gff", file extensions "gtf" and "gff3" are set to be equivalent.
	 * 
	 * @param filter file extension to filter by (see notes on "fa" and "gff").
	 * @param gz whether or not to ignore the ".gz" extension (e.g. if gz=true and filter="bed", then "XXX.bed.gz" will pass the filter, "XXX.bed" will pass the filter, but "XXX.gff" will NOT pass the filter).
	 */
	public ExtensionFileFilter(String filter, boolean gz) {
		ext = filter;
		if(ext.equals("fa")) { ext2 = "fasta"; ext3 = "fsa"; }
		if(ext.equals("gff")) { ext2 = "gtf"; ext3 = "gff3"; }
		includeGZ = gz;
	}

	public boolean accept(File f) {
		if (f.isDirectory()) return true;
		String extension = includeGZ ? getExtensionIgnoreGZ(f) : getExtension(f);
		if (extension != null) {
			if (extension.equals(ext) || extension.equals(ext2) || extension.equals(ext3)) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	/**
	 * Get the extension string from a File object.
	 * 
	 * @param f the input file to get the extension for
	 * @return the file extension string (without "." char)
	 */
	public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');
        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }

	/**
	 * Get the extension string from a File object, ignoring ".gz" extension if present and searching for next extension.
	 * 
	 * @param f the input file to get the extension for
	 * @return the file extension string (without "." char)
	 */
	public static String getExtensionIgnoreGZ(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');
		if (i > 0 &&  i < s.length() - 1) {
			ext = s.substring(i+1).toLowerCase();
			// Get ext before .gz
			if (ext.equals("gz")) {
				s = s.substring(0,i);
				i = s.lastIndexOf('.');
				ext = s.substring(i+1).toLowerCase();
			}
		}
		return ext;
	}

	/**
	 * Get the filename without the last extension (tokenize filename by "." char and pull off last "." character and last token. If no "." characters, return the whole filename)
	 * e.g. f="blahblah.fa" returns "blahblah"
	 * e.g. f="oompaloompa" returns "oompaloompa"
	 * 
	 * @param f the File to strip an extension from
	 * @return string of filename with extension stripped away
	 * @throws IOException
	 */
	public static String stripExtension(File f) throws IOException {
		String[] name = f.getName().split("\\.");
		String NEWNAME = name[0];
		for(int x = 1; x < name.length-1; x++) {
			NEWNAME += ("." + name[x]);
		}
		return(NEWNAME);
	}

	/**
	 * Get the string without the last extension, ignoring any ".gz" extensions (tokenize strings by "." char and pull off last "." character and last token. If no "." characters, return the whole filename)
	 * e.g. f="blahblah.fa" returns "blahblah"
	 * e.g. f="oompaloompa" returns "oompaloompa"
	 * e.g. f="blahblah.fa.gz" returns "blahblah"
	 * e.g. f="oompaloompa.gz" returns "oompaloompa"
	 * 
	 * @param f the File to strip an extension from
	 * @return string of filename with extension stripped away
	 * @throws IOException
	 */
	public static String stripExtensionIgnoreGZ(File f) throws IOException {
		String NEWNAME = f.getName();
		if (NEWNAME.endsWith(".gz")) { NEWNAME = NEWNAME.substring(0, NEWNAME.length()-3); }
		int i = NEWNAME.lastIndexOf('.');
		if (i > 0 &&  i < NEWNAME.length() - 1) {
			NEWNAME = NEWNAME.substring(0,i).toLowerCase();
		}
		return(NEWNAME);
	}

	/**
	 * Get the string without the last extension (tokenize strings by "." char and pull off last "." character and last token. If no "." characters, return the whole filename)
	 * e.g. f="blahblah.fa" returns "blahblah"
	 * e.g. f="oompaloompa" returns "oompaloompa"
	 * 
	 * @param f the String to strip an extension from
	 * @return string of filename with extension stripped away
	 */
	public static String stripExtension(String f) {
		String[] name = f.split("\\.");
		String NEWNAME = name[0];
		for(int x = 1; x < name.length-1; x++) {
			NEWNAME += ("." + name[x]);
		}
		return(NEWNAME);
	}

	/**
	 * Get the string of the cannonical filepath without the last extension. Return every character in the path until the last instance of "." character (not including ".").
	 * e.g. f="/parent/directory/blahblah.fa" returns "/parent/directory/blahblah"
	 * 
	 * @param f the String to strip an extension from
	 * @return string of filename with extension stripped away
	 */
	public static String stripExtensionPath(File f) throws IOException {
		String NAME = f.getCanonicalPath();
		return(NAME.substring(0, NAME.lastIndexOf('.')));
	}
	
	@Override
	public String getDescription() {
		return null;
	}
}
