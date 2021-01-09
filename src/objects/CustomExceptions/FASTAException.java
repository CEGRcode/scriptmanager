package objects.CustomExceptions;

import java.io.File;

@SuppressWarnings("serial")
public class FASTAException extends Exception {
	
	public FASTAException(File fasta) {
		super("FASTA file contains invalid lines: " + fasta.getName());
	}
	
}
