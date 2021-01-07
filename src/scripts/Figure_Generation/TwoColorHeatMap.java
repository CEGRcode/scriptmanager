package scripts.Figure_Generation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class TwoColorHeatMap {

	protected static File SAMPLE = null;

	protected static int startROW = 1;
	protected static int startCOL = 2;
	protected static int pixelHeight = 600;
	protected static int pixelWidth = 200;

	protected static String scaleType = "treeview";
	protected static double quantile = 0.9;
	protected static double absolute = -999;

	public static Color MINCOLOR = new Color(255, 255, 255);
	public static Color MAXCOLOR = new Color(255, 0, 0);

	protected static boolean OUTPUTSTATUS = false;
	protected static File OUTFILE = null;
	protected static String FILEID = null;

	private static ArrayList<double[]> MATRIX = null;
	public static double COLOR_RATIO = 1;

	private JLabel picLabel = null;

	public TwoColorHeatMap(File in, Color c, int startR, int startC, int pHeight, int pWidth, String scale, double abs,
			double quant, File output, boolean outstatus) {

		SAMPLE = in;
		MAXCOLOR = c;
		startROW = startR;
		startCOL = startC;
		pixelHeight = pHeight;
		pixelWidth = pWidth;
		scaleType = scale;

		absolute = abs;
		quantile = quant;

		OUTFILE = output;
		OUTPUTSTATUS = outstatus;
	}

	public void run() throws IOException {

		String FILEID = SAMPLE.getName().split("\\.")[0];

		System.out.println("Loading Matrix file: " + FILEID);
		MATRIX = loadMatrix(SAMPLE);
		System.out.println("Matrix file loaded.");
		System.out.println("Rows detected: " + MATRIX.size());
		System.out.println("Columns detected: " + MATRIX.get(0).length);

		if (scaleType.equalsIgnoreCase("treeview")) {
			ArrayList<double[]> newMatrix = rebinMatrix(MATRIX);
			if (absolute != -999) {
				COLOR_RATIO = absolute;
			} else {
				COLOR_RATIO = getQuantile(newMatrix, quantile);
			}

			System.out.println("Contrast threshold: " + COLOR_RATIO);
			BufferedImage treeMap = generateHeatMap(newMatrix);
			picLabel = new JLabel(new ImageIcon(treeMap));
			// Don't output PNG if OUTPUTSTATUS is false, which is the flag for not
			// outputing figures
			if (OUTPUTSTATUS) {
				ImageIO.write(treeMap, "png", OUTFILE);
			}
		} else if (!scaleType.equalsIgnoreCase("treeview")) {
			// COLOR_RATIO = 2 * getNonZeroAvg(MATRIX);
			if (absolute != -999) {
				COLOR_RATIO = absolute;
			} else {
				COLOR_RATIO = getQuantile(MATRIX, quantile);
			}
			System.out.println("Contrast threshold: " + COLOR_RATIO);

			BufferedImage rawMap = generateHeatMap(MATRIX);
			BufferedImage compressedMap = resize(rawMap, pixelWidth, pixelHeight);
			picLabel = new JLabel(new ImageIcon(compressedMap));

			// Don't output PNG if OUTPUTSTATUS is false, which is the flag for not
			// outputing figures
			if (OUTPUTSTATUS) {
				ImageIO.write(compressedMap, "png", OUTFILE);
			}
		}

	}

	public static BufferedImage generateHeatMap(ArrayList<double[]> matrix) throws FileNotFoundException {
		int width = 1;
		int height = 1;

		int pixwidth = matrix.get(0).length * width;
		int pixheight = matrix.size() * height;

		System.setProperty("java.awt.headless", "true");
		BufferedImage im = new BufferedImage(pixwidth, pixheight, BufferedImage.TYPE_INT_ARGB);
		Graphics g = im.getGraphics();
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(new Color(255, 255, 255));
		g2.fillRect(0, 0, pixwidth, pixheight);

		int count = 0;
		for (int x = 0; x < matrix.size(); x++) {
			double[] ID = matrix.get(x);

			for (int j = 0; j < ID.length; j++) {
				if (ID[j] > 0) {
					double v = ID[j] / COLOR_RATIO;
					double sVal = v > 1 ? 1 : (v < 0 ? 0 : v);
					int red = (int) (MAXCOLOR.getRed() * sVal + MINCOLOR.getRed() * (1 - sVal));
					int green = (int) (MAXCOLOR.getGreen() * sVal + MINCOLOR.getGreen() * (1 - sVal));
					int blue = (int) (MAXCOLOR.getBlue() * sVal + MINCOLOR.getBlue() * (1 - sVal));
					g.setColor(new Color(red, green, blue));
				} else {
					g.setColor(Color.WHITE);
				}
				g.fillRect(j * width, count * height, width, height);
			}
			count++;
		}
		return im;
	}

	public static BufferedImage resize(BufferedImage img, int newW, int newH) {
		BufferedImage resized_image = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = (Graphics2D) resized_image.createGraphics();
		if (scaleType.equalsIgnoreCase("bicubic")) {
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		} else if (scaleType.equalsIgnoreCase("bilinear")) {
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		} else if (scaleType.equalsIgnoreCase("neighbor")) {
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		}
		g2.drawImage(img, 0, 0, newW, newH, null);
		g2.dispose();
		return resized_image;
	}

	public static ArrayList<double[]> rebinMatrix(ArrayList<double[]> oldmatrix) {
		ArrayList<double[]> newmatrix = new ArrayList<double[]>();
		int R = oldmatrix.size();
		int C = oldmatrix.get(0).length;
		int r = pixelHeight;
		int c = pixelWidth;

		// expand the original array to be larger is expanding beyond pixel size
		if (r > R) {
			int rowAdd = (r / R) + 1;
			// System.out.println("Row duplication events: " + rowAdd);
			ArrayList<double[]> temp = new ArrayList<double[]>();
			for (int x = 0; x < R; x++) {
				for (int y = 0; y <= rowAdd; y++) {
					temp.add(oldmatrix.get(x).clone());
				}
			}
			// Set pointers to new row values
			oldmatrix = temp;
			R = oldmatrix.size();
		}
		if (c > C) {
			int colAdd = (c / C) + 1;
			// System.out.println("Column duplication events: " + colAdd);
			ArrayList<double[]> temp = new ArrayList<double[]>();
			for (int x = 0; x < R; x++) {
				double[] oldarray = oldmatrix.get(x);
				double[] newarray = new double[oldarray.length * colAdd];
				for (int y = 0; y < newarray.length; y++) {
					// System.out.print(y + "\t");
					// System.out.println(y / colAdd);
					newarray[y] = oldarray[y / colAdd];
				}
				temp.add(newarray);
			}
			// Set pointers to new row values
			oldmatrix = temp;
			C = oldmatrix.get(0).length;
		}

		int rowDelete = R % r;
		int colDelete = C % c;
		// if(rowDelete != 0) { System.out.println("Rows to delete: " + rowDelete); }
		// if(colDelete != 0) { System.out.println("Cols to delete: " + colDelete); }

		// Matrix resize requires perfect division % == 0; So remove and average in
		// rows/columns to make it happen
		if (rowDelete > 0) {
			// Get indexes of matrix rows evenly spread across the matrix
			// [0 ... 100] down to 20 rows produces [0,5,10,15,20]
			int[] row_delete = linspace(0, R - 1, rowDelete);
			// Correction to remove the last row if linspace output results in out of bounds
			for (int x = 0; x < row_delete.length; x++) {
				if (row_delete[x] == oldmatrix.size() - 1) {
					row_delete[x]--;
				}
			}
			// Get the indexes of matrix rows +1
			int[] row_delete_plus1 = frameshift(row_delete);
			if (row_delete.length != row_delete_plus1.length) {
				System.err.println("Row delete frameshift/merge failure!!!");
				System.exit(1);
			}
			// Merge rows to be deleted into adjacent row by averaging
			for (int x = 0; x < row_delete.length; x++) {
				double[] oldrow = oldmatrix.get(row_delete[x]);
				double[] updatedrow = oldmatrix.get(row_delete_plus1[x]);
				for (int y = 0; y < oldrow.length; y++) {
					updatedrow[y] = (oldrow[y] + updatedrow[y]) / 2.0;
				}
			}
			// Delete redundant rows - go in reverse order to maintain index order
			for (int x = row_delete.length - 1; x >= 0; x--) {
				oldmatrix.remove(row_delete[x]);
			}
			// Set R to new value
			R = oldmatrix.size();
		}

		if (colDelete > 0) {
			int[] col_delete = linspace(0, C - 1, colDelete);
			// Correction to remove the last column if linspace output results in out of
			// bounds
			for (int x = 0; x < col_delete.length; x++) {
				if (col_delete[x] == oldmatrix.get(0).length - 1) {
					col_delete[x]--;
				}
			}
			int[] col_delete_plus1 = frameshift(col_delete);
			if (col_delete.length != col_delete_plus1.length) {
				System.err.println("Column delete frameshift/merge failure!!!");
				System.exit(1);
			}

			// Merge cols to be deleted into adjacent col by averaging
			for (int x = 0; x < oldmatrix.size(); x++) {
				double[] oldcol = oldmatrix.get(x);
				double[] avgcol = oldcol.clone();
				for (int y = 0; y < col_delete.length; y++) {
					avgcol[col_delete_plus1[y]] = (oldcol[col_delete[y]] + oldcol[col_delete_plus1[y]]) / 2.0;
				}

				double[] updatedcol = new double[oldcol.length - col_delete.length];
				int currentIndex = 0;
				for (int y = 0; y < oldcol.length; y++) {
					boolean remove = false;
					for (int z = 0; z < col_delete.length; z++) {
						if (col_delete[z] == y) {
							remove = true;
						}
					}
					if (!remove) {
						updatedcol[currentIndex] = avgcol[y];
						currentIndex++;
					}
				}
				oldmatrix.set(x, updatedcol);
			}
			// Set C to new value
			C = oldmatrix.get(0).length;
		}

		if (R % r != 0) {
			System.err.println("Failure to remove rows modularly!!!");
			System.exit(1);
		}
		if (C % c != 0) {
			System.err.println("Failure to remove columns modularly!!!");
			System.exit(1);
		}

		// Resize original matrix into new matrix size; average values to make new
		// values
		for (int i = 0; i < R; i += (R / r)) {
			double[] newRow = new double[c];
			for (int j = 0; j < C; j += (C / c)) {
				double AVG = 0, count = 0;
				;
				for (int x = i; x < i + (R / r); x++) {
					for (int y = j; y < j + (C / c); y++) {
						AVG += oldmatrix.get(x)[y];
						count++;
					}
				}
				newRow[j / (C / c)] = AVG / count;
			}
			newmatrix.add(newRow);
		}
		return newmatrix;
	}

	public static int[] linspace(int min, int max, int points) {
		int[] d = new int[points];
		if (points < 0) {
			System.err.println("Invalid number of points to parse!!!\n" + points);
			System.exit(1);
		}
		for (int i = 1; i < points; i++) {
			d[i] = min + i * (max - min) / (points - 1);
		}
		return d;
	}

	public static int[] frameshift(int[] orig) {
		int[] newarray = new int[orig.length];
		for (int x = 0; x < orig.length; x++) {
			newarray[x] = orig[x] + 1;
		}
		return newarray;
	}

	public static double getNonZeroAvg(ArrayList<double[]> matrix) {
		double average = 0;
		int count = 0;

		for (int x = 0; x < matrix.size(); x++) {
			for (int y = 0; y < matrix.get(x).length; y++) {
				if (matrix.get(x)[y] != 0) {
					average += matrix.get(x)[y];
					count++;
				}
			}
		}
		if (count != 0) {
			average /= count;
		}
		return average;
	}

	public static double getQuantile(ArrayList<double[]> matrix, double percent) {
		ArrayList<Double> nonZero = new ArrayList<Double>();
		for (int x = 0; x < matrix.size(); x++) {
			for (int y = 0; y < matrix.get(x).length; y++) {
				if (matrix.get(x)[y] != 0) {
					nonZero.add(Double.valueOf(matrix.get(x)[y]));
				}
			}
		}
		Collections.sort(nonZero);
		// Collections.reverse(nonZero);
		int index = (int) (percent * (double) (nonZero.size() - 1));
		return nonZero.get(index);
	}

	public static ArrayList<double[]> loadMatrix(File input) throws UnsupportedEncodingException, IOException {
		ArrayList<double[]> matrix = new ArrayList<double[]>();
		int currentRow = 0;
		if (input.getAbsoluteFile().toString().endsWith(".gz")) {
			BufferedReader scan = new BufferedReader(
					new InputStreamReader(new GZIPInputStream(new FileInputStream(input)), "UTF-8"));
			String line = scan.readLine();
			while (line != null) {
				String[] temp = line.split("\t");
				if (!temp[0].contains("YORF") && currentRow >= startROW) {
					double[] ARRAY = new double[temp.length - startCOL];
					for (int x = startCOL; x < temp.length; x++) {
						ARRAY[x - startCOL] = Double.parseDouble(temp[x]);
					}
					matrix.add(ARRAY);
				}
				currentRow++;
				line = scan.readLine();
			}
			scan.close();
		} else {
			Scanner scan = new Scanner(input);
			while (scan.hasNextLine()) {
				String[] temp = scan.nextLine().split("\t");
				if (!temp[0].contains("YORF") && currentRow >= startROW) {
					double[] ARRAY = new double[temp.length - startCOL];
					for (int x = startCOL; x < temp.length; x++) {
						ARRAY[x - startCOL] = Double.parseDouble(temp[x]);
					}
					matrix.add(ARRAY);
				}
				currentRow++;
			}
			scan.close();
		}
		return matrix;
	}

	public JLabel getImg() {
		return (picLabel);
	}

}
