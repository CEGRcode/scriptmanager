package scriptmanager.util;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.Spring;
import javax.swing.SpringLayout;

/**
 * Helpers for sizing SpringLayout-based tool windows from their content.
 * <p>
 * Tool windows used to call {@code setBounds(x, y, w, h)}, which fixes the
 * <i>outer</i> frame size. The outer size includes the operating system's title
 * bar and borders, and the space taken by text depends on the platform's fonts,
 * so a layout that fit on macOS overlapped or truncated on Windows.
 * {@link #pack} instead measures the content, sizes the window to fit it on any
 * platform, and then leaves resizing to the layout exactly as before:
 * components anchored to the window's right or bottom edge follow it, and an
 * optional "grow" component (typically the file list) takes up any extra height.
 */
public final class WindowLayout {

	/** Gap between the right-most / bottom-most component and the window edge. */
	private static final int PAD = 10;

	private WindowLayout() {
	}

	/**
	 * Sizes the window to fit its content and sets that as its minimum size.
	 * <p>
	 * The content width is {@code minWidth} or, if larger, whatever the given
	 * row-end components need (their right edge plus padding). Components passed
	 * as {@code rowEnds} must be positioned from the left (not anchored to the
	 * content pane's right edge). The content height runs to the bottom edge of
	 * {@code bottom}, so the form must be chained from the top down to
	 * {@code bottom}.
	 * <p>
	 * The content pane's edges are only constrained while measuring. They are
	 * then handed back to the layout, so that when the user resizes the window
	 * the new size is not pushed into the components that defined it.
	 *
	 * @param frame    window to size
	 * @param layout   the content pane's SpringLayout
	 * @param content  the content pane
	 * @param minWidth minimum content width in pixels (the width the window was
	 *                 originally designed for)
	 * @param bottom   bottom-most component (typically the run button)
	 * @param grow     component that absorbs extra height when the window is
	 *                 made taller (typically the file list's scroll pane; it must
	 *                 have only a NORTH constraint), or {@code null} to keep the
	 *                 {@code bottom} row at the bottom of the window instead
	 * @param rowEnds  left-anchored components at the end of rows whose width
	 *                 depends on text, e.g. the last radio button in a row
	 */
	public static void pack(JFrame frame, SpringLayout layout, JComponent content, int minWidth, Component bottom,
			JComponent grow, Component... rowEnds) {
		Spring width = Spring.constant(minWidth);
		for (Component c : rowEnds) {
			width = Spring.max(width, Spring.sum(layout.getConstraint(SpringLayout.EAST, c), Spring.constant(PAD)));
		}
		pack(frame, layout, content, width, bottom, grow);
	}

	/**
	 * As {@link #pack(JFrame, SpringLayout, JComponent, int, Component, JComponent, Component...)},
	 * but with the content width given as a spring, for rows whose width cannot be
	 * expressed as a left-anchored row end (e.g. a row with a right-aligned control).
	 *
	 * @param frame   window to size
	 * @param layout  the content pane's SpringLayout
	 * @param content the content pane
	 * @param width   spring giving the content width; must not depend on the
	 *                content pane's own right edge
	 * @param bottom  bottom-most component (typically the run button)
	 * @param grow    component that absorbs extra height, or {@code null}
	 */
	public static void pack(JFrame frame, SpringLayout layout, JComponent content, Spring width, Component bottom,
			JComponent grow) {
		// Fix the grow component's natural height so it does not change when, for
		// example, files are added to a list.
		if (grow != null) {
			grow.setPreferredSize(grow.getPreferredSize());
		}

		// 1. Measure: temporarily define the content pane's right and bottom edges
		// from its components and ask the layout for the resulting size.
		SpringLayout.Constraints pane = layout.getConstraints(content);
		pane.setConstraint(SpringLayout.EAST, width);
		pane.setConstraint(SpringLayout.SOUTH,
				Spring.sum(layout.getConstraint(SpringLayout.SOUTH, bottom), Spring.constant(PAD / 2)));
		Dimension size = layout.preferredLayoutSize(content);

		// 2. Release: drop those constraints so the layout manages the content
		// pane's edges itself, as it did when the window had a fixed size.
		layout.removeLayoutComponent(content);

		// 3. Let any extra height go to the grow component, and keep the bottom row
		// at the bottom of the window (whichever puts it lower).
		if (grow != null) {
			int base = grow.getPreferredSize().height;
			layout.getConstraints(grow).setHeight(new ExtraHeightSpring(content, size.height, base));
		}
		SpringLayout.Constraints row = layout.getConstraints(bottom);
		int top = row.getY().getPreferredValue();
		row.setY(Spring.max(row.getY(), new ExtraHeightSpring(content, size.height, top)));

		// 4. Size the window from the measured content (pack() adds this platform's
		// title bar and borders) and do not allow it to be made smaller.
		content.setPreferredSize(size);
		frame.pack();
		frame.setMinimumSize(frame.getSize());
	}

	/**
	 * A spring whose value is a base amount plus however much taller the content
	 * pane currently is than its packed height. It reads the content pane's
	 * actual height at layout time and never feeds a value back into other
	 * springs, so resizing cannot disturb the rest of the layout.
	 */
	private static final class ExtraHeightSpring extends Spring {
		private final JComponent content;
		private final int packedHeight;
		private final int base;

		ExtraHeightSpring(JComponent content, int packedHeight, int base) {
			this.content = content;
			this.packedHeight = packedHeight;
			this.base = base;
		}

		private int value() {
			return base + Math.max(0, content.getHeight() - packedHeight);
		}

		@Override
		public int getMinimumValue() {
			return base;
		}

		@Override
		public int getPreferredValue() {
			return base;
		}

		@Override
		public int getMaximumValue() {
			return Integer.MAX_VALUE / 2;
		}

		@Override
		public int getValue() {
			return value();
		}

		@Override
		public void setValue(int value) {
			// Value is derived from the window size; nothing to store.
		}
	}
}
