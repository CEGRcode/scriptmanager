package scripts.Sequence_Analysis;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class FASTARandomize {
	
	public static File randomizeFASTA(File FASTA, File out) throws IOException {
		String[] name = FASTA.getName().split("\\.");
		String NEWNAME = "";
		for(int x = 0; x < name.length - 1; x++) {
			if(x == name.length - 2) { NEWNAME += (name[x] + "_RAND.fa"); }
			else { NEWNAME += (name[x] + "."); }
		}
		File RAND = new File(out + File.separator + NEWNAME);
		
		Random randnum = new Random();
		PrintStream OUT = new PrintStream(RAND);
		Scanner scan = new Scanner(FASTA);
		while (scan.hasNextLine()) {
			String HEADER = scan.nextLine();
			OUT.println(HEADER);
			if(HEADER.contains(">")) {
				String[] SEQ = scan.nextLine().split("");
				ArrayList<String> SEQ_ARRAY = new ArrayList<String>();
				for(int x = 0; x < SEQ.length; x++) { SEQ_ARRAY.add(SEQ[x]); }
				for(int x = 0; x < SEQ.length; x++) {
					int randIndex = randnum.nextInt(SEQ_ARRAY.size());
					OUT.print(SEQ_ARRAY.get(randIndex));
					SEQ_ARRAY.remove(randIndex);
				}
				OUT.println();
				
			} else { OUT.println("ERROR - NOT FASTA FORMAT"); }
		}
		
		OUT.close();
		scan.close();
		return RAND;
	}
		
}