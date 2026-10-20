package io.github.kormcrete.flatlaf.lagoon;

import com.formdev.flatlaf.ui.FlatComboBoxUI;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.plaf.ComponentUI;
import java.awt.Container;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;

import static com.formdev.flatlaf.util.UIScale.scale;

/**
 * Combo boxes whose arrow is a small raised button ({@link LagoonArrowButton}) set inside the frame with
 * a one-pixel gap, instead of a band separated by a line. The box keeps FlatLaf's size: the button and
 * its gaps take the width FlatLaf gives its square button. Pair with {@code ComboBox.buttonStyle = none},
 * so FlatLaf paints no button background of its own.
 */
public class LagoonComboBoxUI extends FlatComboBoxUI {

    public static ComponentUI createUI(JComponent c) {
        return new LagoonComboBoxUI();
    }

    @Override
    protected JButton createArrowButton() {
        return new LagoonArrowButton(SwingConstants.SOUTH, 7, 4);
    }

    @Override
    protected LayoutManager createLayoutManager() {
        LayoutManager flat = super.createLayoutManager();
        return new DelegatingLayout(flat) {
            @Override
            public void layoutContainer(Container parent) {
                super.layoutContainer(parent);
                if (arrowButton == null || !arrowButton.isVisible()) {
                    return;
                }
                Insets insets = comboBox.getInsets();
                int gap = scale(1);
                int slot = arrowButton.getWidth();
                int size = slot - 2 * gap;
                int x = arrowButton.getX() + gap;
                int y = insets.top + gap;
                int h = comboBox.getHeight() - insets.top - insets.bottom - 2 * gap;
                arrowButton.setBounds(x, y, size, h);
                if (editor != null && comboBox.isEditable()) {
                    editor.setBounds(rectangleForCurrentValue());
                }
            }
        };
    }

    @Override
    protected Rectangle rectangleForCurrentValue() {
        Rectangle r = super.rectangleForCurrentValue();
        if (arrowButton != null && arrowButton.isVisible() && comboBox.getComponentOrientation().isLeftToRight()) {
            // up to the gap before the button, not to where FlatLaf's button band began
            int end = arrowButton.getX() - scale(1);
            if (end > r.x) {
                r.width = Math.min(r.width, end - r.x);
            }
        }
        return r;
    }
}
