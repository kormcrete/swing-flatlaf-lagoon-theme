package io.github.kormcrete.flatlaf.lagoon;

import com.formdev.flatlaf.ui.FlatLabelUI;
import com.formdev.flatlaf.ui.FlatUIUtils;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.plaf.ComponentUI;
import java.awt.Dimension;

/** Labels sized by {@link CompoundSize}: the icon-text gap is kept after the text, icon or not. */
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

    @Override
    public Dimension getMinimumSize(JComponent c) {
        return adjust(c, super.getMinimumSize(c));
    }

    private static Dimension adjust(JComponent c, Dimension size) {
        JLabel l = (JLabel) c;
        return CompoundSize.adjust(l, size, l.getText(), l.getIcon(), l.getIconTextGap(),
                l.getHorizontalTextPosition(), l.getVerticalTextPosition());
    }
}
