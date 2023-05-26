package scriptmanager.objects;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Polygon;

import java.util.ArrayList;

import javax.swing.JPanel;

/**
 * Component that displays a graphic of a sequenced DNA fragment with annotated
 * Read1, Read2, and dynamically updating encoding reference points marked by
 * arrows.
 *
 * @author Olivia Lang
 * @see scriptmanager.window_interface.Read_Analysis.TagPileupWindow
 */
@SuppressWarnings("serial")
public class ReadFragmentCartoon extends JPanel {

	// Define constants of cartoon like dimensions
	int readHeight = 20;
	int readWidth = 100;
	public int cartoonHeight = 80;
	public int arrowHeight = 15;
	public int arrowWidth = 6;
	int fragmentLength = 350;
	int fragmentThickness = 3;
	int edgePad = 20;
	int verticalPad;

	public ArrayList<Polygon> arrows = new ArrayList<Polygon>(6);

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Determine distance from top of graphic
		verticalPad = (cartoonHeight - 2*(readHeight) - fragmentThickness - arrowHeight) / 2 + arrowHeight;

		// Draw Read 1
		g.setColor(Color.CYAN);
		g.fillRect(edgePad, verticalPad, readWidth, readHeight);

		// Draw Read 2
		g.setColor(Color.ORANGE);
		g.fillRect(edgePad+fragmentLength - readWidth, verticalPad + readHeight + fragmentThickness, readWidth, readHeight);

		// Draw Fragment
		g.setColor(Color.BLACK);
		g.fillRect(edgePad, verticalPad + readHeight, fragmentLength, fragmentThickness);

		// Label Reads
		g.drawString("Read1", edgePad, verticalPad + readHeight - 1);
		g.drawString("Read2", edgePad + fragmentLength - 37, verticalPad + 2*readHeight + fragmentThickness - 1);

		// Draw arrows
		for (int p=0; p<arrows.size(); p++) {
			g.fillPolygon(arrows.get(p));
		}
	}

	/**
	 * Update graphic to reflect changes in aspect and read encodings.
	 *
	 * @param aspect encoding for read aspect (5', 3', midpoint, full frag) as defined by PileupParameters
	 * @param read encoding for read output (R1, R2, or both) as defined by PileupParameters
	 * @see scriptmanager.objects.PileupParameters
	 */
	public void redrawArrows(int aspect, int read) {
		verticalPad = (cartoonHeight - (2 * readHeight) - fragmentThickness - arrowHeight) / 2 + arrowHeight;
		arrows = new ArrayList<Polygon>(2);
		int topY = verticalPad - arrowHeight;
		if (aspect == PileupParameters.FRAGMENT) { // handle fragment
//			arrows.add(bigArrow());
			for (int i = 0; i < fragmentLength; i += 6) {
				arrows.add(arrow(edgePad + i, topY));
			}
		} else if (aspect == PileupParameters.MIDPOINT) { // handle midpoint
			arrows.add(arrow(edgePad + fragmentLength / 2, topY));
		} else if (aspect == PileupParameters.THREE) { // handle 3 prime ends
			if (read == PileupParameters.READ1 || read == PileupParameters.ALLREADS) {
				arrows.add(arrow(edgePad + readWidth, topY));
			}
			if (read == PileupParameters.READ2 || read == PileupParameters.ALLREADS) {
				arrows.add(arrow(edgePad + fragmentLength - readWidth, topY));
			}
		} else if (aspect == PileupParameters.FIVE) { // handle 5 prime ends
			if (read == PileupParameters.READ1 || read == PileupParameters.ALLREADS) {
				arrows.add(arrow(edgePad, topY));
			}
			if (read == PileupParameters.READ2 || read == PileupParameters.ALLREADS) {
				arrows.add(arrow(edgePad + fragmentLength, topY));
			}
		}
		repaint();
	}

	/**
	 * A rightward-pointing arrow across length of DNA fragment. (unused)
	 * <br>
	 * Legacy code for how Full Fragment was initially represented.
	 *
	 * @return a Polygon object describing the coordinates of a large,
	 *         right-pointing arrow
	 */
	public Polygon bigArrow() {
		int arrowThickness = 5;
		int arrowHead = 20;
		int arrowflank = (arrowHeight - arrowThickness)/2;

		int xPoly[] = {edgePad, edgePad+fragmentLength-arrowHead, edgePad+fragmentLength-arrowHead,
				edgePad+fragmentLength, edgePad +fragmentLength -arrowHead, edgePad+fragmentLength-arrowHead, edgePad};
		int yPoly[] = {verticalPad-arrowflank,  verticalPad-arrowflank, verticalPad,  verticalPad-arrowHeight/2,
				verticalPad-arrowHeight,  verticalPad-arrowHeight+arrowflank, verticalPad-arrowHeight+arrowflank};
		return(new Polygon(xPoly,yPoly,xPoly.length));
	}

	/**
	 * Draw downward-pointing arrow at (x,y) coordinate (upper-center) for marking
	 * where on the DNA fragment coverage is tallied on this graphic. Arrow size is
	 * arrowHeight tall (15px) and flank*2 (6px) wide.
	 * <pre>
	 *   (x,y)
	 *    ||
	 *    ||
	 *    ||
	 *    ||
	 *   \  /
	 *    \/
	 * </pre>
	 *
	 * @param x center (width) coordinate of arrow to draw
	 * @param y top coordinate of arrow to draw
	 * @return a Polygon object describing a small down-pointing arrow
	 */
	public Polygon arrow(int x,int y) {

		// arrowhead flank size
		int flank = arrowWidth/2;
		// arrow thickness
		int thick = 2;
		// arrowhead height
		int head = 5;

		x = x-thick/2;

		int xPoly[] = {x,  x,  x-flank,  x+thick/2,  x+thick+flank,  x+thick,  x+thick};
		int yPoly[] = {y,  y+arrowHeight-head,  y+arrowHeight-head, y+arrowHeight, y+arrowHeight-head, y+arrowHeight-head, y};
		return(new Polygon(xPoly,yPoly,xPoly.length));
	}

	/**
	 * Override function to set preferred size (800 x cartoonHeight).
	 */
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(400,cartoonHeight);
	}
}
