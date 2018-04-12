package scripts.File_Utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.bind.DatatypeConverter;

public class MD5Checksum {
	public static String calculateMD5(String input) throws IOException, NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(Files.readAllBytes(Paths.get(input)));
		byte[] digest = md.digest();
		return DatatypeConverter.printHexBinary(digest).toLowerCase();
	}
}
