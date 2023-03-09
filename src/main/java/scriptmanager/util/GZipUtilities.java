package scriptmanager.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.zip.GZIPInputStream;

/**
 * This class contains static methods for parsing files and input streams to
 * determine GZip-format information. Used across many tools in the scripts
 * package.
 * 
 * Found on a stackoverflow thread
 * (https://stackoverflow.com/questions/30507653/how-to-check-whether-file-is-gzip-or-not-in-java)
 *
 * @author Olivia Lang
 */
public final class GZipUtilities {

	/**
	 * Checks if InputStream is gzipped by reading the next two bytes for the GZip
	 * MAGIC value and then resets the file marker to the original start position.
	 *
	 * @param input the InputStream to check if Gzipped (assumes stream starts at
	 *              the beginning of the file).
	 * @return Returns true if bytes match GZip MAGIC and false if not.
	 * @throws FileNotFoundException
	 */
	public static boolean isGZipped(InputStream in) {
		if (!in.markSupported()) {
			in = new BufferedInputStream(in);
		}
		in.mark(2); //mark current position in stream
		int magic = 0;
		try {
			magic = in.read() & 0xff | ((in.read() << 8) & 0xff00);
			in.reset(); //return to marked position in stream
		} catch (IOException e) {
			e.printStackTrace(System.err);
			return false;
		}
		return magic == GZIPInputStream.GZIP_MAGIC;
	}
	
	/**
	 * Checks if File is gzipped by opening and reading the first two bytes for the
	 * GZip MAGIC value and then closing the file.
	 * 
	 * @param f the File to check if Gzipped.
	 * @return Returns true if bytes match GZip MAGIC and false if not.
	 */
	public static boolean isGZipped(File f) {
		int magic = 0;
		try {
			RandomAccessFile raf = new RandomAccessFile(f, "r");
			magic = raf.read() & 0xff | ((raf.read() << 8) & 0xff00);
			raf.close();
		} catch (Throwable e) {
			e.printStackTrace(System.err);
		}
		return magic == GZIPInputStream.GZIP_MAGIC;
	}
}
