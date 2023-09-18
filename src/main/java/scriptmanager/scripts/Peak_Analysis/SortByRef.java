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
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import scriptmanager.objects.CoordinateObjects.BEDCoord;
import scriptmanager.objects.CoordinateObjects.GFFCoord;
import scriptmanager.objects.CoordinateObjects.GenomicCoord;
import scriptmanager.util.GZipUtilities;

public class SortByRef {
	private PrintStream PS = null;
	private PrintStream OUT = null;
	private File PEAK = null;
	private File REF = null;
	private boolean PROPER_STRANDED = false;
	private long MAX_LEFT = 0;
	private long MAX_RIGHT = 0;
	private boolean BOUNDED_LEFT = false;
	private boolean BOUNDED_RIGHT = false;

	private long[][] matches = null;
	
	public SortByRef(File ref, File peak, File out, boolean properStrands, boolean gzOutput, PrintStream ps, String leftBound, String rightBound) throws IOException {
		PS = ps;
		OUT = GZipUtilities.makePrintStream(out, gzOutput);
		PEAK = peak;
		REF = ref;
		PROPER_STRANDED = properStrands;
		if (!leftBound.equals("")){
			BOUNDED_LEFT = true;
			MAX_LEFT = Long.parseLong(leftBound);
		}
		if (!rightBound.equals("")){
			BOUNDED_RIGHT = true;
			MAX_RIGHT= Long.parseLong(rightBound);
		}
	}
		
	public void sortGFF() throws IOException, InterruptedException {
		printPS("Mapping: " + PEAK + " to " + REF);
		printPS("Starting: " + getTimeStamp());

		//Create a HashMap for peak coords and an ArrayList for reference coords
		HashMap<String, HashMap<Long, GFFCoord>> peakCoords = new HashMap<String, HashMap<Long, GFFCoord>>();
		ArrayList<GFFCoord> refCoords = new ArrayList<GFFCoord>();
		
		//load peak coords into map
		BufferedReader br = GZipUtilities.makeReader(PEAK);
		String line;
		HashMap<Long, GFFCoord> currentMap;
		GFFCoord currentCoord;
		Long currentIndex = (long)0;
		while((line = br.readLine()) != null) {
			currentCoord = new GFFCoord(line);
			if (!peakCoords.containsKey(currentCoord.getChrom())){
				peakCoords.put(currentCoord.getChrom(), currentMap = new HashMap<>());
				currentMap.put(currentIndex, currentCoord);
			} else {
				peakCoords.get(currentCoord.getChrom()).put(currentIndex, currentCoord);
			}
			currentCoord.calcMid();
			currentIndex++;
		}
		br.close();

		//load ref coords into array
		br = GZipUtilities.makeReader(REF);
		line = "";
		while((line = br.readLine()) != null) {
			refCoords.add(currentCoord = new GFFCoord(line));
			currentCoord.calcMid();
		}
		br.close();

		//Makes matching array
		long[][] matches = new long[refCoords.size()][3];

		//Iterate through reference coords, matching to closest valid peak coord index
		for (int i = 0; i < refCoords.size(); i++){
			GFFCoord refCoord  = refCoords.get(i);
			long minDiff = Long.MAX_VALUE;
			long peakIndex = -1;
			//Get peak coords with matching chr
			HashMap<Long, GFFCoord> peakCoordMap = peakCoords.get(refCoord.getChrom());
			for (Map.Entry<Long, GFFCoord> coord : peakCoordMap.entrySet()){
				GFFCoord peakCoord = coord.getValue();
				if (validateCoord(peakCoord, refCoord, minDiff))
				{
					//Store difference and index in original file
					minDiff = Math.abs(peakCoord.getMid() - refCoord.getMid());
					peakIndex = coord.getKey();
				}
			}
			matches[i] = new long[] {minDiff, peakIndex, i};
			System.out.println(Arrays.toString(matches[i]));
		}

		//Go through all peak values
		for (int i = 0; i < currentIndex; i++){
			//Get all of the matching coordinates
			ArrayList<Long[]> refMatches = new ArrayList<Long[]>();
			for(long[] match: matches){
				if (match[1] == i){
					refMatches.add(ArrayUtils.toObject(match));
				}
			}
			//Sort by distance
			Collections.sort(refMatches, (a, b) -> Long.compare(a[0], b[0]));
			//Print the coordinates, closest to furthest
			for (Long[] coord: refMatches){
				OUT.println(refCoords.get(coord[2].intValue()));
			}
			if (i % 1000 == 0){
				printPS("Reference rows processed: " + i);
			}
		}

		//Print invalid coords
		for(long[] coord: matches){
			if(coord[1] == -1){
				OUT.println(refCoords.get((int)coord[2]));
			}
		}
		System.out.println(refCoords.size());
		OUT.close();
		
		printPS("Completing: " + getTimeStamp());
	}

	public void sortBED() throws IOException, InterruptedException {
		printPS("Mapping: " + PEAK + " to " + REF);
		printPS("Starting: " + getTimeStamp());

		//Create a HashMap for peak coords and an ArrayList for reference coords
		HashMap<String, HashMap<Long, BEDCoord>> peakCoords = new HashMap<String, HashMap<Long, BEDCoord>>();
		ArrayList<BEDCoord> refCoords = new ArrayList<BEDCoord>();
		
		//load peak coords into map
		BufferedReader br = GZipUtilities.makeReader(PEAK);
		String line;
		HashMap<Long, BEDCoord> currentMap;
		BEDCoord currentCoord;
		Long currentIndex = (long)0;
		while((line = br.readLine()) != null) {
			currentCoord = new BEDCoord(line);
			if (!peakCoords.containsKey(currentCoord.getChrom())){
				peakCoords.put(currentCoord.getChrom(), currentMap = new HashMap<>());
				currentMap.put(currentIndex, currentCoord);
			} else {
				peakCoords.get(currentCoord.getChrom()).put(currentIndex, currentCoord);
			}
			currentCoord.calcMid();
			currentIndex++;
		}
		br.close();

		//load ref coords into array
		br = GZipUtilities.makeReader(REF);
		line = "";
		while((line = br.readLine()) != null) {
			refCoords.add(currentCoord = new BEDCoord(line));
			currentCoord.calcMid();
		}
		br.close();

		//Makes matching array
		long[][] matches = new long[refCoords.size()][3];

		//Iterate through reference coords, matching to closest valid peak coord index
		for (int i = 0; i < refCoords.size(); i++){
			BEDCoord refCoord  = refCoords.get(i);
			long minDiff = Long.MAX_VALUE;
			long peakIndex = -1;
			//Get peak coords with matching chr
			HashMap<Long, BEDCoord> peakCoordMap = peakCoords.get(refCoord.getChrom());
			for (Map.Entry<Long, BEDCoord> coord : peakCoordMap.entrySet()){
				BEDCoord peakCoord = coord.getValue();
				if (validateCoord(peakCoord, refCoord, minDiff))
				{
					//Store difference and index in original file
					minDiff = Math.abs(peakCoord.getMid() - refCoord.getMid());
					peakIndex = coord.getKey();
				}
			}
			matches[i] = new long[] {minDiff, peakIndex, i};
			System.out.println(Arrays.toString(matches[i]));
		}

		//Go through all peak values
		for (int i = 0; i < currentIndex; i++){
			//Get all of the matching coordinates
			ArrayList<Long[]> refMatches = new ArrayList<Long[]>();
			for(long[] match: matches){
				if (match[1] == i){
					refMatches.add(ArrayUtils.toObject(match));
				}
			}
			//Sort by distance
			Collections.sort(refMatches, (a, b) -> Long.compare(a[0], b[0]));
			//Print the coordinates, closest to furthest
			for (Long[] coord: refMatches){
				OUT.println(refCoords.get(coord[2].intValue()));
			}
			if (i % 1000 == 0){
				printPS("Reference rows processed: " + i);
			}
		}

		//Print invalid coords
		for(long[] coord: matches){
			if(coord[1] == -1){
				OUT.println(refCoords.get((int)coord[2]));
			}
		}
		System.out.println(refCoords.size());
		OUT.close();

		printPS("Completing: " + getTimeStamp());
	}

	private boolean validateCoord(GenomicCoord peak, GenomicCoord ref, long minDistance){
		boolean properDir = (!PROPER_STRANDED || peak.getDir().equals(ref.getDir()));
		boolean closer = (minDistance >= Math.abs(peak.getMid() - ref.getMid()));
		boolean inBounds = true;
		if (peak.getMid() <= ref.getMid() && BOUNDED_RIGHT){
			inBounds = ref.getMid() - peak.getMid() <= MAX_RIGHT;
		} else if (BOUNDED_LEFT) {
			inBounds = peak.getMid() - ref.getMid() <= MAX_LEFT;
		}
		return properDir && closer && inBounds;
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