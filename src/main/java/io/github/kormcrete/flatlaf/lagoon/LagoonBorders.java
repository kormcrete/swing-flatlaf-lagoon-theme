package io.github.kormcrete.flatlaf.lagoon;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatButtonBorder;
import com.formdev.flatlaf.ui.FlatMenuBarBorder;
import com.formdev.flatlaf.ui.FlatRoundBorder;
import com.formdev.flatlaf.ui.FlatScrollPaneBorder;
import com.formdev.flatlaf.ui.FlatTextBorder;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import static com.formdev.flatlaf.util.UIScale.scale;

/**
 * Component borders whose dark frame sits in an etched ring: a shadow line above and to the left, a
 * highlight line below and to the right, in every enabled state; a disabled component has no ring.
 *
 * <p>The ring takes the space FlatLaf reserves as {@code Component.focusWidth} for its focus ring,
 * which these borders never paint: focus does not change the frame. Colours:
 * {@code Component.ringShadowColor} and {@code Component.ringHighlightColor}. The properties file names
 * these classes as the {@code *.border} of fields, combo boxes, spinners, buttons and scroll panes;
 * {@link MenuBar} is the menu bar's, which has no ring.
 */
public final class LagoonBorders {

    private LagoonBorders() {
    }

    static void paintRing(Component c, Graphics g, int x, int y, int width, int height, int focusWidth) {
        Color shadow = UIManager.getColor("Component.ringShadowColor");
        Color highlight = UIManager.getColor("Component.ringHighlightColor");
        if (shadow == null || highlight == null || focusWidth <= 0 || !c.isEnabled()) {
            return;
        }
        int t = scale(focusWidth);
        g.setColor(shadow);
        g.fillRect(x, y, width - t, t);
        g.fillRect(x, y + t, t, height - 2 * t);
        g.setColor(highlight);
        g.fillRect(x + width - t, y, t, height);
        g.fillRect(x, y + height - t, width - t, t);
    }

    /** Text fields, password fields and formatted text fields. */
    public static class Text extends FlatTextBorder {
        @Override
        protected Color getFocusColor(Component c) {
            return null;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            paintRing(c, g, x, y, width, height, getFocusWidth(c));
            super.paintBorder(c, g, x, y, width, height);
        }
    }

    /** Combo boxes and spinners. */
    public static class Round extends FlatRoundBorder {
        @Override
        protected Color getFocusColor(Component c) {
            return null;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            paintRing(c, g, x, y, width, height, getFocusWidth(c));
            super.paintBorder(c, g, x, y, width, height);
        }
    }

    /** Scroll panes. */
    public static class ScrollPane extends FlatScrollPaneBorder {
        @Override
        protected Color getFocusColor(Component c) {
            return null;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            paintRing(c, g, x, y, width, height, getFocusWidth(c));
            super.paintBorder(c, g, x, y, width, height);
        }
    }

    /** Buttons and toggle buttons; only those that paint a frame get a ring, never a toolbar button. */
    public static class Button extends FlatButtonBorder {
        @Override
        protected Color getFocusColor(Component c) {
            return null;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            if (paintsFrame(c)) {
                paintRing(c, g, x, y, width, height, getFocusWidth(c));
            }
            super.paintBorder(c, g, x, y, width, height);
        }

        /** The cases in which {@link FlatButtonBorder} paints its frame, from public API. */
        static boolean paintsFrame(Component c) {
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

    /** The menu bar: FlatLaf's bottom line, with {@code MenuBar.padding} added around the items. */
    public static class MenuBar extends FlatMenuBarBorder {
        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets = super.getBorderInsets(c, insets);
            Insets padding = UIManager.getInsets("MenuBar.padding");
            if (padding != null) {
                insets.top += scale(padding.top);
                insets.left += scale(padding.left);
                insets.bottom += scale(padding.bottom);
                insets.right += scale(padding.right);
            }
            return insets;
        }
    }
}
