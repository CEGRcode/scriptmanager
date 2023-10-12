package scriptmanager.scripts.Read_Analysis;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.swing.JOptionPane;

import scriptmanager.util.GZipUtilities;

public class TransposeMatrix {

	private File MATRIX = null;
	private File OUTFILE = null;
	private int ROWINDEX = 0;
	private int COLINDEX = 0;
	private boolean OUTPUT_GZIP = false;

	/**
	 * Creates a new instance of the TransposeMatrix script with a given input file
	 * @param m TAB file to be scaled
	 * @param o Output directory/file
	 * @param r Starting row (zero-indexed)
	 * @param c Starting column (zero-indexed)
	 * @param z Output Gzip
	 */
	public TransposeMatrix(File matrix, File output, int row, int column, boolean gzOutput) {
		MATRIX = matrix;
		OUTFILE = output;
		ROWINDEX = row;
		COLINDEX = column;
		OUTPUT_GZIP = gzOutput;
	}

	/**
	 * Transposes the given matrix
	 * @throws IOException Invalid file or parameters
	 */
	public void run() throws IOException {

		System.err.println(getTimeStamp());
		System.err.println("Processing file:\t" + MATRIX.getName());
		System.err.println("Starting row index:\t" + ROWINDEX);
		System.err.println("Starting column index:\t" + COLINDEX);

		try {
			// Parse, scale, and output tab-delimited matrix on the fly
			BufferedReader br = makeReader();
			//Determine number of rows and columns
			int numCols = br.readLine().split("\t").length;
			int numRows = 1;
			while (br.readLine() != null){
				numRows++;
			}

			br = makeReader();
			PrintStream OUT = makePrintStream(OUTFILE, OUTPUT_GZIP);
			//Transfer not-transposed rows
			for (int i = 1; i <= ROWINDEX; i++){
				OUT.println(br.readLine());
			}
			//Load original matrix into array
			String[][] originalMatrix = new String[numRows - ROWINDEX][numCols];
			String line;
			int row = 0;
			while ((line = br.readLine()) != null){
				originalMatrix[row++] = line.split("\t");
			}
			int outputRow = ROWINDEX;
			br.close();

			//Iterate through the original file's columns
			for (int currentCol = COLINDEX; currentCol < numCols; currentCol++) {
				//Create a newLine array to represent a row of output file
				String[] newLine = new String[numRows - ROWINDEX + COLINDEX];
				int outputCol = COLINDEX;
				for (int currentRow = ROWINDEX; currentRow < numRows; currentRow++){
					//Get skipped columns from appropriate row
					if (outputRow == currentRow){
						for(int i = 0; i < COLINDEX; i++){
							newLine[i] = originalMatrix[currentRow - ROWINDEX][i];
						}
					} 
					newLine[outputCol] = originalMatrix[currentRow - ROWINDEX][currentCol];
					outputCol++;
				}
				//Write the column to the new file as a row
				for(int val = 0; val < newLine.length; val++){
					if(newLine[val] == null){
						newLine[val] = "";
					}
					OUT.print(newLine[val] + ((val == newLine.length - 1) ? "\n" : "\t"));
				}
				outputRow++;
			}
			
			//If rows are left over after transposing (due to labels)
			if (numRows > numCols){
				//Directly print the remaining rows to the output file
				for(int remainingRows = outputRow; remainingRows < numRows; remainingRows++){
					String[] newLine = new String[COLINDEX];
					for(int i = 0; i < COLINDEX; i++){
						newLine[i] = originalMatrix[remainingRows - ROWINDEX][i];
					}
					for(int val = 0; val < newLine.length; val++){
						OUT.print(newLine[val] + ((val == newLine.length - 1) ? "\n" : "\t"));
					}
				}
			}
			OUT.close();
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(null, MATRIX.getName() + " contains non-numbers in indexes selected!!!");
		}
	}

	private static String getTimeStamp() {
		Date date = new Date();
		String time = new Timestamp(date.getTime()).toString();
		return time;
	}

	private BufferedReader makeReader() throws IOException{
		if(GZipUtilities.isGZipped(MATRIX)) {
			return new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(MATRIX)), "UTF-8"));
		} else {
			return new BufferedReader(new InputStreamReader(new FileInputStream(MATRIX), "UTF-8"));
		}
	}

	private PrintStream makePrintStream(File o, boolean gzip) throws IOException{
		if(gzip){
			return new PrintStream(new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(o)))); 
		} else {
			return new PrintStream(new BufferedOutputStream(new FileOutputStream(o)));
		}
	}
}
