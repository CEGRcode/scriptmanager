package scripts;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import edu.psu.compbio.seqcode.genome.Genome;
import edu.psu.compbio.seqcode.genome.Organism;
import edu.psu.compbio.seqcode.genome.location.StrandedRegion;
import edu.psu.compbio.seqcode.gse.gsebricks.verbs.sequence.FASTALoader;
import edu.psu.compbio.seqcode.gse.gsebricks.verbs.sequence.SequenceGenerator;
import edu.psu.compbio.seqcode.gse.tools.utils.Args;
import edu.psu.compbio.seqcode.gse.utils.ArgParser;
import edu.psu.compbio.seqcode.gse.utils.NotFoundException;
import edu.psu.compbio.seqcode.gse.utils.Pair;
import edu.psu.compbio.seqcode.projects.shaun.Utilities;

/**
 * @author Adapted from Dr. Shaun Mahony
 */

public class FourColor {

	
	public FourColor(){}
	
	/**
	 * Visualize sequences as color pixels
	 * @param seqs, raw sequences or FASTA sequences
	 * @param width, width of each base, in pixel
	 * @param height, height of each base, in pixel
	 * @param f, output file
	 */
	public static void visualizeSequences(List<String> seqs, int width, int height, File f){
		if (seqs.size()==0)
			return;
		
		int pixheight = 0;
		int maxLen = 0;
		for (String s:seqs){
        	if (s.length()!=0 && s.charAt(0)!='>')	{		// ignore header line of FASTA file
        		pixheight += height;
        		if (maxLen < s.length())
        			maxLen = s.length();
        	}
		}
		int pixwidth = maxLen*width;
		
		System.setProperty("java.awt.headless", "true");
		BufferedImage im = new BufferedImage(pixwidth, pixheight,BufferedImage.TYPE_INT_ARGB);
        Graphics g = im.getGraphics();
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(Color.WHITE);
        g2.fillRect(0,0,pixwidth, pixheight);
        
        int count = 0;
        for (String s:seqs){
        	if (s.charAt(0)=='>')			// ignore header line of FASTA file
        		continue;
        	char[] letters = s.toCharArray();
        	for (int j=0;j<letters.length;j++){
        		switch(letters[j]){
        		case 'A':
        		case 'a':
        			g.setColor(Color.RED);
        			break;
        		case 'C':
        		case 'c':
                    g.setColor(Color.BLUE);
        			break;
        		case 'G':
        		case 'g':
                    g.setColor(Color.ORANGE);
        			break;
        		case 'T':
        		case 't':
                    g.setColor(Color.GREEN);
        			break;
        		case '-':
                    g.setColor(Color.WHITE);
        			break;
                default:
                	g.setColor(Color.GRAY);
        		}
                g.fillRect(j*width, count*height, width, height);
        	}
            count++;
        }
        try {
            ImageIO.write(im,"png",f);
        }  catch (IOException ex) {
            ex.printStackTrace();
        }
	}
	
	public static void main(String[] args) {
		ArgParser ap = new ArgParser(args);
        if(!ap.hasKey("species")) { 
            System.err.println("Usage:\n " +
                               "SequenceAlignmentFigure \n" +
                               " Required: \n" +
                               "  --species <species;version> AND --gen <sequence directory> AND" +
                               "  --peaks <file containing stranded coordinates> OR" +
                               "  --seq <FASTA file>\n" +
                               "  --win <window of sequence around peaks> \n"+
                               "  --out output filename\n" +
                               "  --seqout sequence output filename\n" +
                               "");
            return;
        }
        try {
        	Pair<Organism, Genome> pair = Args.parseGenome(args);
        	Organism currorg = pair.car();
        	Genome currgen = pair.cdr();
        	String outFile = ap.hasKey("out") ? ap.getKeyValue("out") : "out.png";
        	String seqOutFile = ap.hasKey("seqout") ? ap.getKeyValue("seqout") : null;
        	
        	List<String> seqs = null;
        	
        	if(ap.hasKey("peaks")){
        		String genomeSequencePath = ap.hasKey("gen") ? ap.getKeyValue("gen") : null;
        		SequenceGenerator seqgen = new SequenceGenerator(currgen);
        		if(genomeSequencePath != null){
        			seqgen.useCache(true);
        			seqgen.useLocalFiles(true);
        			seqgen.setGenomePath(genomeSequencePath);
        		}
		        String peaksFile = ap.getKeyValue("peaks");
		    	int win = ap.hasKey("win") ? new Integer(ap.getKeyValue("win")).intValue():-1;
		    	List<StrandedRegion> regions = Utilities.loadStrandedRegionsFromMotifFile(currgen, peaksFile, win);
		    	seqs = Utilities.getSequencesForStrandedRegions(regions, seqgen);
        	}else if(ap.hasKey("seq")){
        		String seqsFile = ap.getKeyValue("seq");
        		FASTALoader loader = new FASTALoader();
        		File f = new File(seqsFile);
        		seqs = new ArrayList<String>();
        		Iterator<Pair<String, String>> it = loader.execute(f);
        		while(it.hasNext()){
        			Pair<String, String> p = it.next();
        			String name = p.car();
        			String seq = p.cdr();
        			seqs.add(seq);
        		}
        	}
        	
        	if(seqs !=null){
		    	SequenceAlignmentFigure fig = new SequenceAlignmentFigure();
		    	fig.visualizeSequences(seqs, 3, 1, new File(outFile));
		    	
		    	if(seqOutFile != null){
		    		FileWriter fout = new FileWriter(seqOutFile);
		    		for(String s: seqs){
		    			fout.write(String.format("%s\n", s));
		    		}
		    		fout.close();
		    	}
        	}
	    	
        } catch (NotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}