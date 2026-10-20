package io.github.kormcrete.flatlaf.lagoon;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatButtonBorder;
import com.formdev.flatlaf.ui.FlatRoundBorder;
import com.formdev.flatlaf.ui.FlatScrollPaneBorder;
import com.formdev.flatlaf.ui.FlatTextBorder;
import com.formdev.flatlaf.ui.FlatUIUtils;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;

import static com.formdev.flatlaf.util.UIScale.scale;

/**
 * Component borders with a light ring outside the dark frame, in every enabled state.
 *
 * <p>FlatLaf reserves {@code Component.focusWidth} around a frame and paints it only while the component
 * has focus. These borders paint it always, in {@code Component.ringColor}, so the frame reads as set
 * into the panel; a disabled component has no ring. The theme sets {@code Component.focusColor} to the
 * same colour, so gaining focus changes nothing here. The properties file names these classes as the
 * {@code *.border} of fields, combo boxes, spinners, buttons and scroll panes.
 */
public final class LagoonBorders {

    private LagoonBorders() {
    }

    static void paintRing(Component c, Graphics g, int x, int y, int width, int height, int focusWidth, int arc) {
        Color ring = UIManager.getColor("Component.ringColor");
        if (ring == null || focusWidth <= 0 || !c.isEnabled()) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            FlatUIUtils.setRenderingHints(g2);
            FlatUIUtils.paintOutlinedComponent(g2, x, y, width, height,
                    scale((float) focusWidth), 1, 0, 0, scale((float) arc), ring, null, null);
        } finally {
            g2.dispose();
        }
    }

    /** Text fields, password fields and formatted text fields. */
    public static class Text extends FlatTextBorder {
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            paintRing(c, g, x, y, width, height, getFocusWidth(c), getArc(c));
            super.paintBorder(c, g, x, y, width, height);
        }
    }

    /** Combo boxes and spinners. */
    public static class Round extends FlatRoundBorder {
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            paintRing(c, g, x, y, width, height, getFocusWidth(c), getArc(c));
            super.paintBorder(c, g, x, y, width, height);
        }
    }

    /** Scroll panes. */
    public static class ScrollPane extends FlatScrollPaneBorder {
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            paintRing(c, g, x, y, width, height, getFocusWidth(c), getArc(c));
            super.paintBorder(c, g, x, y, width, height);
        }
    }

    /** Buttons and toggle buttons; only those that paint a frame get a ring, never a toolbar button. */
    public static class Button extends FlatButtonBorder {
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            if (paintsFrame(c)) {
                paintRing(c, g, x, y, width, height, getFocusWidth(c), getArc(c));
            }
            super.paintBorder(c, g, x, y, width, height);
        }

        /** The cases in which {@link FlatButtonBorder} paints its frame, from public API. */
        private static boolean paintsFrame(Component c) {
            if (!(c instanceof AbstractButton) || !((AbstractButton) c).isContentAreaFilled()
                    || c.getParent() instanceof JToolBar) {
                return false;
            }
            Object type = ((JComponent) c).getClientProperty(FlatClientProperties.BUTTON_TYPE);
            return type == null || !(FlatClientProperties.BUTTON_TYPE_TOOLBAR_BUTTON.equals(type)
                    || FlatClientProperties.BUTTON_TYPE_BORDERLESS.equals(type)
                    || FlatClientProperties.BUTTON_TYPE_HELP.equals(type)
                    || FlatClientProperties.BUTTON_TYPE_TAB.equals(type));
        }
    }
}
