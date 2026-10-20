package io.github.kormcrete.flatlaf.lagoon;

import com.formdev.flatlaf.ui.FlatScrollBarUI;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;

/**
 * Scroll bars with raised arrow buttons ({@link LagoonArrowButton}) and a framed thumb, set off from
 * the content they scroll by a line on the side facing it: left of a vertical bar (right, right to
 * left), above a horizontal one. {@code ScrollBar.width} includes that line.
 *
 * <p>Colours: {@code ScrollBar.lineColor} for the line, {@code ScrollBar.thumbBorderColor} for the
 * thumb's frame.
 */
public class LagoonScrollBarUI extends FlatScrollBarUI {

    public static ComponentUI createUI(JComponent c) {
        return new LagoonScrollBarUI();
    }

    @Override
    protected void installDefaults() {
        super.installDefaults();
        Border border = scrollbar.getBorder();
        if (border == null || border instanceof UIResource) {
            scrollbar.setBorder(new Line());
        }
    }

    @Override
    protected void uninstallDefaults() {
        if (scrollbar.getBorder() instanceof Line) {
            scrollbar.setBorder(null);
        }
        super.uninstallDefaults();
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return new Button(orientation);
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return new Button(orientation);
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        super.paintThumb(g, c, thumbBounds);
        Color frame = UIManager.getColor("ScrollBar.thumbBorderColor");
        if (frame == null || thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
            return;
        }
        // Thumb insets are given for a vertical bar, as FlatLaf reads them.
        Insets insets = thumbInsets;
        if (scrollbar.getOrientation() == JScrollBar.HORIZONTAL) {
            insets = new Insets(insets.right, insets.top, insets.left, insets.bottom);
        }
        Rectangle r = FlatUIUtils.subtractInsets(thumbBounds, UIScale.scale(insets));
        g.setColor(frame);
        FlatUIUtils.paintOutline((Graphics2D) g, r.x, r.y, r.width, r.height, UIScale.scale(1f), 0);
    }

    /** The arrow buttons, square to the bar's breadth inside the line. */
    private class Button extends LagoonArrowButton {

        Button(int direction) {
            super(direction, 7, 4);
        }

        @Override
        public Dimension getPreferredSize() {
            if (!isShowButtons()) {
                return new Dimension();
            }
            int breadth = UIScale.scale(scrollBarWidth) - UIScale.scale(1);
            return new Dimension(breadth, breadth);
        }
    }

    /** The line on the side facing the content. */
    private static final class Line extends AbstractBorder implements UIResource {

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.top = insets.left = insets.bottom = insets.right = 0;
            int t = UIScale.scale(1);
            if (!vertical(c)) {
                insets.top = t;
            } else if (c.getComponentOrientation().isLeftToRight()) {
                insets.left = t;
            } else {
                insets.right = t;
            }
            return insets;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Color line = UIManager.getColor("ScrollBar.lineColor");
            if (line == null) {
                return;
            }
            int t = UIScale.scale(1);
            g.setColor(line);
            if (!vertical(c)) {
                g.fillRect(x, y, width, t);
            } else if (c.getComponentOrientation().isLeftToRight()) {
                g.fillRect(x, y, t, height);
            } else {
                g.fillRect(x + width - t, y, t, height);
            }
        }

        private static boolean vertical(Component c) {
            return !(c instanceof JScrollBar) || ((JScrollBar) c).getOrientation() == SwingConstants.VERTICAL;
        }
    }
}
