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

import objects.ToolDescriptions;
//import scripts.File_Utilities.MD5Checksum;

/**
	File_UtilitiesCLI/MD5ChecksumCLI
*/
@Command(name = "md5checksum", mixinStandardHelpOptions = true,
	description = ToolDescriptions.md5checksum_description,
	exitCodeOnInvalidInput = 1,
	exitCodeOnExecutionException = 1)
public class MD5ChecksumCLI implements Callable<Integer> {
	
	@Override
	public Integer call() throws Exception {
		System.err.println( ">MD5ChecksumCLI.call()" );
		String validate = validateInput();
		if(!validate.equals("")){
			System.err.println( validate );
			System.err.println("Invalid input. Check usage using '-h' or '--help'");
			System.exit(1);
		}
		
		//SEStats.getSEStats( output, bamFile, null );
		
		//System.err.println("Calculations Complete");
		return(0);
	}
	
	private String validateInput() throws IOException {
		String r = "";
		//validate input here
		//append messages to the user to `r`
		return(r);
	}
}
