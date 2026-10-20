package io.github.kormcrete.flatlaf.lagoon;

import com.formdev.flatlaf.ui.FlatButtonUI;
import com.formdev.flatlaf.ui.FlatToggleButtonUI;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import java.awt.Dimension;

/** Buttons sized by {@link CompoundSize}: the icon-text gap kept after the text, and an odd size. */
public class LagoonButtonUI extends FlatButtonUI {

    public static ComponentUI createUI(JComponent c) {
        // not shared: FlatLaf decides sharing on a check it keeps to itself
        return new LagoonButtonUI();
    }

    protected LagoonButtonUI() {
        super(false);
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        return adjust(c, super.getPreferredSize(c));
    }

    static Dimension adjust(JComponent c, Dimension size) {
        AbstractButton b = (AbstractButton) c;
        return CompoundSize.odd(CompoundSize.adjust(b, size, b.getText(), b.getIcon(), b.getIconTextGap(),
                b.getHorizontalTextPosition(), b.getVerticalTextPosition()));
    }

    /** Toggle buttons, sized as buttons are. */
    public static class Toggle extends FlatToggleButtonUI {

        public static ComponentUI createUI(JComponent c) {
            return new Toggle();
        }

        protected Toggle() {
            super(false);
        }

        @Override
        public Dimension getPreferredSize(JComponent c) {
            return adjust(c, super.getPreferredSize(c));
        }
    }
}
