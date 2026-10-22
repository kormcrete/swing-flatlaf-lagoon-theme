package io.github.kormcrete.flatlaf.lagoon;

import com.formdev.flatlaf.ui.FlatTabbedPaneUI;
import com.formdev.flatlaf.util.UIScale;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;

/**
 * Card tabs where the unselected tabs are framed and shaded too, so the selected one reads as
 * lifted onto the content. FlatLaf paints the selected tab as usual.
 *
 * <p>Colours: {@code TabbedPane.unselectedBorderColor} for the frame and
 * {@code TabbedPane.unselectedBottomColor}, where the shade ends (it starts at the tab background).
 * {@code TabbedPane.contentMargins} is the room between the content's frame and the content, given as
 * insets for tabs on top (its top is the side next to the tabs) and turned with the tabs; the frame
 * itself stays one line.
 *
 * <p>{@code TabbedPane.selectedTabRaise} (pixels; 0 for none) lowers the unselected tabs from the outer edge
 * by that much, so the selected tab stands above its neighbours; their title, icon or tab component stays
 * centred in what is left.
 */
public class LagoonTabbedPaneUI extends FlatTabbedPaneUI {

    private Color unselectedBorderColor;
    private Color unselectedBottomColor;
    private Insets contentMargins;
    private int selectedTabRaise;

    public static ComponentUI createUI(JComponent c) {
        return new LagoonTabbedPaneUI();
    }

    @Override
    protected void installDefaults() {
        super.installDefaults();
        unselectedBorderColor = UIManager.getColor("TabbedPane.unselectedBorderColor");
        unselectedBottomColor = UIManager.getColor("TabbedPane.unselectedBottomColor");
        contentMargins = UIManager.getInsets("TabbedPane.contentMargins");
        selectedTabRaise = UIManager.getInt("TabbedPane.selectedTabRaise");
    }

    // FlatLaf paints the frame from the separator height, not from these insets, so the room added here stays blank.
    @Override
    protected Insets getContentBorderInsets(int tabPlacement) {
        Insets insets = super.getContentBorderInsets(tabPlacement);
        if (contentMargins == null || (insets.top == 0 && insets.left == 0 && insets.bottom == 0 && insets.right == 0)) {
            return insets;
        }
        Insets pad = new Insets(0, 0, 0, 0);
        rotateInsets(UIScale.scale(contentMargins), pad, tabPlacement);
        return new Insets(insets.top + pad.top, insets.left + pad.left,
                insets.bottom + pad.bottom, insets.right + pad.right);
    }

    @Override
    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                                      int x, int y, int w, int h, boolean isSelected) {
        if (!isSelected) {
            Rectangle r = lowered(tabPlacement, x, y, w, h);
            x = r.x; y = r.y; w = r.width; h = r.height;
        }
        if (isSelected || unselectedBottomColor == null || isHover(tabIndex)) {
            super.paintTabBackground(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
            return;
        }
        // Light on the outer edge, darker towards the content, whichever side the content is on.
        Color light = tabPane.getBackground();
        GradientPaint shade;
        switch (tabPlacement) {
            case BOTTOM: shade = new GradientPaint(x, y + h, light, x, y, unselectedBottomColor); break;
            case LEFT:   shade = new GradientPaint(x, y, light, x + w, y, unselectedBottomColor); break;
            case RIGHT:  shade = new GradientPaint(x + w, y, light, x, y, unselectedBottomColor); break;
            default:     shade = new GradientPaint(x, y, light, x, y + h, unselectedBottomColor); break;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setPaint(shade);
            g2.fillRect(x, y, w, h);
        } finally {
            g2.dispose();
        }
    }

    @Override
    protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
                                  int x, int y, int w, int h, boolean isSelected) {
        if (!isSelected) {
            Rectangle r = lowered(tabPlacement, x, y, w, h);
            x = r.x; y = r.y; w = r.width; h = r.height;
        }
        if (isSelected || unselectedBorderColor == null) {
            super.paintTabBorder(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
            return;
        }
        // Outer and trailing edges, plus the leading edge on the first tab only, so neighbours share
        // one line. The edge facing the content belongs to the content frame.
        int line = UIScale.scale(1);
        boolean vertical = tabPlacement == LEFT || tabPlacement == RIGHT;
        boolean first = tabIndex == 0;
        g.setColor(unselectedBorderColor);
        if (vertical) {
            g.fillRect(tabPlacement == LEFT ? x : x + w - line, y, line, h);   // outer
            g.fillRect(x, y + h - line, w, line);                              // trailing (below)
            if (first) g.fillRect(x, y, w, line);                              // leading (above)
        } else {
            g.fillRect(x, tabPlacement == TOP ? y : y + h - line, w, line);    // outer
            g.fillRect(x + w - line, y, line, h);                              // trailing (right)
            if (first) g.fillRect(x, y, line, h);                              // leading (left)
        }
    }

    @Override
    protected int getTabLabelShiftX(int tabPlacement, int tabIndex, boolean isSelected) {
        int shift = super.getTabLabelShiftX(tabPlacement, tabIndex, isSelected);
        if (isSelected) return shift;
        int half = raise() / 2;
        return tabPlacement == LEFT ? shift + half : tabPlacement == RIGHT ? shift - half : shift;
    }

    @Override
    protected int getTabLabelShiftY(int tabPlacement, int tabIndex, boolean isSelected) {
        int shift = super.getTabLabelShiftY(tabPlacement, tabIndex, isSelected);
        if (isSelected) return shift;
        int half = raise() / 2;
        return tabPlacement == TOP ? shift + half : tabPlacement == BOTTOM ? shift - half : shift;
    }

    // FlatLaf lays out a title and icon here without the label shift (only tab components get it), so an
    // unselected tab's label is moved here, to stay centred in the lowered tab.
    @Override
    protected String layoutAndClipLabel(int tabPlacement, FontMetrics metrics, int tabIndex, String title,
                                        Icon icon, Rectangle tabRect, Rectangle iconRect, Rectangle textRect,
                                        boolean isSelected) {
        String clipped = super.layoutAndClipLabel(tabPlacement, metrics, tabIndex, title, icon, tabRect,
                iconRect, textRect, isSelected);
        int dx = getTabLabelShiftX(tabPlacement, tabIndex, isSelected) - super.getTabLabelShiftX(tabPlacement, tabIndex, isSelected);
        int dy = getTabLabelShiftY(tabPlacement, tabIndex, isSelected) - super.getTabLabelShiftY(tabPlacement, tabIndex, isSelected);
        iconRect.translate(dx, dy);
        textRect.translate(dx, dy);
        return clipped;
    }

    /** An unselected tab's bounds: moved off the outer edge by the raise, the side facing the content kept. */
    private Rectangle lowered(int tabPlacement, int x, int y, int w, int h) {
        int raise = raise();
        switch (tabPlacement) {
            case BOTTOM: return new Rectangle(x, y, w, h - raise);
            case LEFT:   return new Rectangle(x + raise, y, w - raise, h);
            case RIGHT:  return new Rectangle(x, y, w - raise, h);
            default:     return new Rectangle(x, y + raise, w, h - raise);
        }
    }

    private int raise() {
        return UIScale.scale(selectedTabRaise);
    }

    private boolean isHover(int tabIndex) {
        return getRolloverTab() == tabIndex;
    }
}
