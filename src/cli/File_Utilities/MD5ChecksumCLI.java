package cli.File_Utilities;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.Date;

import scripts.File_Utilities.MD5Checksum;

/**
	File_UtilitiesCLI/MD5ChecksumCLI
*/
@Command(name = "md5checksum", mixinStandardHelpOptions = true,
		description = "Calculate MD5 checksum for an input file")
public class MD5ChecksumCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "The file we want to calculate the MD5checksum for")
	private File input;

	@Option(names = {"-o", "--output"}, description = "specify output filepath")
	private File output = new File("md5checksum.txt");
	
// 	public MD5ChecksumCLI(){
// 		System.out.println("MD5ChecksumCLI-constructor:");
// 		System.out.println("bamFile = " + bamFile.toString());
// 		System.out.println("output = " + output.toString());
// 	}
	
	@Override
	public Integer call() throws Exception {
		System.out.println( ">MD5ChecksumCLI.call()" );
// 		MD5Checksum.calculateMD5(String input);
		
//		if(OUTPUT_PATH == null) OUT = new PrintStream("md5checksum.txt");
//		else OUT = new PrintStream(OUTPUT_PATH + File.separator + "md5checksum.txt");
		PrintStream OUT = new PrintStream( output );
		String md5hash = MD5Checksum.calculateMD5(input.getAbsolutePath());
		OUT.println("MD5 (" + input.getName() + ") = " + md5hash);
		
		return(0);
	}
	
}

