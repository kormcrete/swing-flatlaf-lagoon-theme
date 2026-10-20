package io.github.kormcrete.flatlaf.lagoon;

import com.formdev.flatlaf.ui.FlatLabelUI;
import com.formdev.flatlaf.ui.FlatUIUtils;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.ComponentUI;
import javax.swing.tree.TreeCellRenderer;
import java.awt.Dimension;

/**
 * Labels sized by {@link CompoundSize}: the icon-text gap is kept after the text, icon or not, table
 * cells included. List and tree renderers keep FlatLaf's size.
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
        // A list or tree renderer draws a list item, combo value or tree row: its size is the item's, not a
        // label's. A table cell keeps the gap, so a column fitted to its widest cell has a label's room.
        if (c instanceof ListCellRenderer || c instanceof TreeCellRenderer) {
            return size;
        }
        JLabel l = (JLabel) c;
        return CompoundSize.adjust(l, size, l.getText(), l.getIcon(), l.getIconTextGap(),
                l.getHorizontalTextPosition(), l.getVerticalTextPosition());
    }
}
