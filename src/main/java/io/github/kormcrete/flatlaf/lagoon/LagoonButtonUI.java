package io.github.kormcrete.flatlaf.lagoon;

import com.formdev.flatlaf.ui.FlatButtonUI;
import com.formdev.flatlaf.ui.FlatToggleButtonUI;
import com.formdev.flatlaf.ui.FlatUIUtils;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;

import static com.formdev.flatlaf.util.UIScale.scale;

/**
 * Buttons sized by {@link CompoundSize} (the icon-text gap kept after the text, and an odd size) and
 * faced with {@link LagoonSheen}. A button with a background of its own, a toolbar button and a disabled
 * one are painted as FlatLaf paints them.
 */
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

    @Override
    protected void paintBackground(Graphics g, JComponent c) {
        if (!paintSheen(g, c, false)) {
            super.paintBackground(g, c);
        }
    }

    static boolean paintSheen(Graphics g, JComponent c, boolean selected) {
        if (!c.isEnabled() || !(c.getBackground() instanceof UIResource) || !LagoonBorders.Button.paintsFrame(c)) {
            return false;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            FlatUIUtils.setRenderingHints(g2);
            float fw = FlatUIUtils.getBorderFocusWidth(c);
            float arc = FlatUIUtils.getBorderArc(c);
            float bw = scale(1f);
            float w = c.getWidth() - 2 * fw;
            float h = c.getHeight() - 2 * fw;
            LagoonSheen.paint(g2, new RoundRectangle2D.Float(fw, fw, w, h, arc, arc),
                    fw + bw, fw + bw, w - 2 * bw, h - 2 * bw,
                    LagoonSheen.pressed(((AbstractButton) c).getModel(), selected));
        } finally {
            g2.dispose();
        }
        return true;
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

        @Override
        protected void paintBackground(Graphics g, JComponent c) {
            if (!paintSheen(g, c, ((AbstractButton) c).isSelected())) {
                super.paintBackground(g, c);
            }
        }
    }
}
