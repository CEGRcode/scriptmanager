package scriptmanager.scripts.File_Utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Checksum {
	public static String calculateMD5(String input) throws IOException, NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(Files.readAllBytes(Paths.get(input)));
		byte byteData[] = md.digest();
        StringBuffer hexString = new StringBuffer();
        for(int i = 0; i < byteData.length; i++) {
        	String hex = Integer.toHexString(0xff & byteData[i]);
        	if(hex.length() == 1) { hexString.append('0'); }
        	hexString.append(hex);
        }
        return hexString.toString();
		
	}
}
