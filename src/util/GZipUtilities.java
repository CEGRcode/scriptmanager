package util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.zip.GZIPInputStream;

/**
 * This class contains static methods for parsing files and input streams to determine GZip-format information.
 * 
 * Found on a stackoverflow thread (https://stackoverflow.com/questions/30507653/how-to-check-whether-file-is-gzip-or-not-in-java)
 *
 */
public final class GZipUtilities {

	/**
	 * Checks if InputStream is gzipped by reading the first two bytes for the GZip MAGIC value.
	 *
	 * @param input
	 * @return
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
	 * Checks if File is gzipped by reading the first two bytes for the GZip MAGIC value.
	 * 
	 * @param f
	 * @return
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
