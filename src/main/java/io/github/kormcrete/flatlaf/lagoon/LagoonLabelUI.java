package io.github.kormcrete.flatlaf.lagoon;

import com.formdev.flatlaf.ui.FlatLabelUI;
import com.formdev.flatlaf.ui.FlatUIUtils;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.ComponentUI;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import java.awt.Dimension;

/**
 * Labels sized by {@link CompoundSize}: the icon-text gap is kept after the text, icon or not. Labels
 * that are cell renderers keep FlatLaf's size.
 */
public class LagoonLabelUI extends FlatLabelUI {

    public static ComponentUI createUI(JComponent c) {
        return FlatUIUtils.canUseSharedUI(c)
                ? FlatUIUtils.createSharedUI(LagoonLabelUI.class, () -> new LagoonLabelUI(true))
                : new LagoonLabelUI(false);
    }

    protected LagoonLabelUI(boolean shared) {
        super(shared);
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        return adjust(c, super.getPreferredSize(c));
    }

    // No getMinimumSize: Swing's derives it from getPreferredSize, gap included.

    private static Dimension adjust(JComponent c, Dimension size) {
        // A renderer draws a cell, list item or combo value: its size is the cell's, not a label's.
        if (c instanceof ListCellRenderer || c instanceof TableCellRenderer || c instanceof TreeCellRenderer) {
            return size;
        }
        JLabel l = (JLabel) c;
        return CompoundSize.adjust(l, size, l.getText(), l.getIcon(), l.getIconTextGap(),
                l.getHorizontalTextPosition(), l.getVerticalTextPosition());
    }
}
