package util;

import java.io.File;
import java.io.IOException;

import javax.swing.filechooser.FileFilter;

public class ExtensionFileFilter extends FileFilter{
	//Filters files such as BAI, BAM, BED, CDT, FASTA, FA, GFF, TAB
		
	private String ext = "";
	private String ext2 = "";
	private String ext3 = "";
	private boolean includeGZ = false;
	
	public ExtensionFileFilter(String filter) {
		ext = filter;
		if(ext.equals("fa")) { ext2 = "fasta"; ext3 = "fsa"; }
		if(ext.equals("gff")) { ext2 = "gtf"; ext3 = "gff3"; }
	}
	
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
	
	public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');
        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
	
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

	public static String stripExtension(File f) throws IOException {
		String[] name = f.getName().split("\\.");
		String NEWNAME = name[0];
		for(int x = 1; x < name.length-1; x++) {
			NEWNAME += ("." + name[x]);
		}
		return(NEWNAME);
	}

	public static String stripExtensionIgnoreGZ(File f) throws IOException {
		String NEWNAME = f.getName();
		if (NEWNAME.endsWith(".gz")) { NEWNAME = NEWNAME.substring(0, NEWNAME.length()-3); }
		int i = NEWNAME.lastIndexOf('.');
		if (i > 0 &&  i < NEWNAME.length() - 1) {
			NEWNAME = NEWNAME.substring(0,i).toLowerCase();
		}
		return(NEWNAME);
	}
	
	public static String stripExtensionPath(File f) throws IOException {
		String NAME = f.getCanonicalPath();
		return(NAME.substring(0, NAME.lastIndexOf('.')));
	}
	
	@Override
	public String getDescription() {
		return null;
	}
}
