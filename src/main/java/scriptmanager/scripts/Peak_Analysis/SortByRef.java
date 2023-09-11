package scriptmanager.scripts.Peak_Analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.lang3.ArrayUtils;

import scriptmanager.objects.CoordinateObjects.BEDCoord;
import scriptmanager.objects.CoordinateObjects.GenomicCoord;
import scriptmanager.util.GZipUtilities;

public class SortByRef {
	private PrintStream PS = null;
	private PrintStream OUT = null;
	private File PEAK = null;
	private File REF = null;
	private boolean PROPER_STRANDED = false;

	private long[][] scores = null;
	
	public SortByRef(File ref, File peak, File out, boolean properStrands, boolean gzOutput, PrintStream ps) throws IOException {
		PS = ps;
		OUT = GZipUtilities.makePrintStream(out, gzOutput);
		PEAK = peak;
		REF = ref;
		PROPER_STRANDED = properStrands;
	}
		
	public void sortGFF() throws IOException, InterruptedException {

	}

	public void sortBED() throws IOException, InterruptedException {
		ArrayList<BEDCoord> peakCoords = new ArrayList<BEDCoord>();
		// HashMap<String, ArrayList<BEDCoord>> peakCoords = new HashMap<String, ArrayList<BEDCoord>>();
		ArrayList<BEDCoord> refCoords = new ArrayList<BEDCoord>();
		// HashMap<String, ArrayList<BEDCoord>> refCoords = new HashMap<String, ArrayList<BEDCoord>>();
		BufferedReader br = GZipUtilities.makeReader(PEAK);
		//load peak coords into array
		String line;
		while((line = br.readLine()) != null) {
			peakCoords.add(new BEDCoord(line));
			((BEDCoord)peakCoords.get(peakCoords.size() - 1)).calcMid();
		}
		br.close();

		br = GZipUtilities.makeReader(REF);
		//load ref coords into array
		line = "";
		while((line = br.readLine()) != null) {
			refCoords.add(new BEDCoord(line));
			((BEDCoord)refCoords.get(refCoords.size() - 1)).calcMid();
		}
		br.close();
		scores = new long[refCoords.size()][3];

		for (int i = 0; i < refCoords.size(); i++){
			BEDCoord refCoord  = refCoords.get(i);
			long minMidpointDiff = Long.MAX_VALUE;
			long minIndex = -1;
			for (int k = 0; k < peakCoords.size(); k++){
				BEDCoord peakCoord = peakCoords.get(k);
					if ((!PROPER_STRANDED || peakCoord.getDir().equals(refCoord.getDir())) && peakCoord.getChrom().equals(refCoord.getChrom())){
						if (minMidpointDiff > Math.abs(peakCoords.get(k).getMid() - refCoords.get(i).getMid()))
						{
							minMidpointDiff = Math.abs(peakCoords.get(k).getMid() - refCoords.get(i).getMid());
							minIndex = k;
						}
					}
				}
				scores[i] = new long[] {minMidpointDiff, minIndex, i};
			}

		for (long k = 0; k < peakCoords.size(); k++){
			ArrayList<Long[]> closestRefScores = new ArrayList<Long[]>();
			for(long[] ref: scores){
				if (ref[1] == k){
					closestRefScores.add(ArrayUtils.toObject(ref));
				}
			}
			Collections.sort(closestRefScores, (a, b) -> Long.compare(a[0], b[0]));
			for (Long[] coord: closestRefScores){
				OUT.println(refCoords.get(coord[2].intValue()));
			}
		}

		for(long[] coord: scores){
			if(coord[1] == -1){
				OUT.println(refCoords.get((int)coord[2]));
			}
		}
		System.out.println(refCoords.size());
		OUT.close();
	}


	private static String getTimeStamp() {
		Date date= new Date();
		String time = new Timestamp(date.getTime()).toString();
		return time;
	}
	
	private void printPS(String message){
		if(PS!=null) PS.println(message);
		System.err.println(message);
	}
}