package io.github.kormcrete.flatlaf.lagoon;

import com.formdev.flatlaf.ui.FlatTabbedPaneUI;
import com.formdev.flatlaf.util.UIScale;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

/**
 * Card tabs where the unselected tabs are framed and shaded too, so the selected one reads as
 * lifted onto the content. FlatLaf paints the selected tab as usual.
 *
 * <p>Colours: {@code TabbedPane.unselectedBorderColor} for the frame and
 * {@code TabbedPane.unselectedBottomColor}, where the shade ends (it starts at the tab background).
 * {@code TabbedPane.contentMargins} is the room between the content's frame and the content, given as
 * insets for tabs on top (its top is the side next to the tabs) and turned with the tabs; the frame
 * itself stays one line.
 */
public class LagoonTabbedPaneUI extends FlatTabbedPaneUI {

    private Color unselectedBorderColor;
    private Color unselectedBottomColor;
    private Insets contentMargins;

    public static ComponentUI createUI(JComponent c) {
        return new LagoonTabbedPaneUI();
    }

    @Override
    protected void installDefaults() {
        super.installDefaults();
        unselectedBorderColor = UIManager.getColor("TabbedPane.unselectedBorderColor");
        unselectedBottomColor = UIManager.getColor("TabbedPane.unselectedBottomColor");
        contentMargins = UIManager.getInsets("TabbedPane.contentMargins");
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

    private boolean isHover(int tabIndex) {
        return getRolloverTab() == tabIndex;
    }
}
