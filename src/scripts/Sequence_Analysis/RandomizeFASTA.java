package scripts.Sequence_Analysis;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class RandomizeFASTA {

	public static File randomizeFASTA(File FASTA, File RANDOUT) throws IOException {
		Random randnum = new Random();
		PrintStream OUT = new PrintStream(RANDOUT);
		Scanner scan = new Scanner(FASTA);
		while (scan.hasNextLine()) {
			String HEADER = scan.nextLine();
			OUT.println(HEADER);
			if (HEADER.contains(">")) {
				String[] SEQ = scan.nextLine().split("");
				ArrayList<String> SEQ_ARRAY = new ArrayList<String>();
				for (int x = 0; x < SEQ.length; x++) {
					SEQ_ARRAY.add(SEQ[x]);
				}
				for (int x = 0; x < SEQ.length; x++) {
					int randIndex = randnum.nextInt(SEQ_ARRAY.size());
					OUT.print(SEQ_ARRAY.get(randIndex));
					SEQ_ARRAY.remove(randIndex);
				}
				OUT.println();

			} else {
				OUT.println("ERROR - NOT FASTA FORMAT");
				System.err.println("ERROR - NOT FASTA FORMAT");
			}
		}

		OUT.close();
		scan.close();
		return RANDOUT;
	}

}