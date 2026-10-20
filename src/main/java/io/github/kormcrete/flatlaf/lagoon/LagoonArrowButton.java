package io.github.kormcrete.flatlaf.lagoon;

import com.formdev.flatlaf.ui.FlatArrowButton;
import com.formdev.flatlaf.ui.FlatUIUtils;

import javax.swing.SwingConstants;
import javax.swing.UIManager;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;

import static com.formdev.flatlaf.util.UIScale.scale;

/**
 * The small raised button of a combo box or spinner: its own rounded frame, a sheen and a solid
 * triangle with a light emboss. Disabled, it is flat with a grey frame and arrow. The combo box and
 * spinner delegates place it inside their frame with a one-pixel gap.
 */
class LagoonArrowButton extends FlatArrowButton {

    private final int arrowWidth;
    private final int arrowHeight;

    LagoonArrowButton(int direction, int arrowWidth, int arrowHeight) {
        super(direction, "triangle", UIManager.getColor("ComboBox.buttonArrowColor"),
                UIManager.getColor("ComboBox.buttonDisabledArrowColor"), null, null, null, null);
        this.arrowWidth = arrowWidth;
        this.arrowHeight = arrowHeight;
        setFocusable(false);
        setRequestFocusEnabled(false);
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            FlatUIUtils.setRenderingHints(g2);
            int w = getWidth();
            int h = getHeight();
            float t = scale(1f);
            float arc = scale(3f);
            RoundRectangle2D.Float outer = new RoundRectangle2D.Float(0, 0, w, h, arc, arc);
            boolean enabled = isEnabled();
            if (enabled) {
                LagoonSheen.State state = isPressed() ? LagoonSheen.State.PRESSED
                        : isHover() ? LagoonSheen.State.ROLLOVER : LagoonSheen.State.NORMAL;
                LagoonSheen.paint(g2, outer, t, t, w - 2 * t, h - 2 * t, state);
            } else {
                g2.setColor(UIManager.getColor("Button.disabledBackground"));
                g2.fill(outer);
            }
            g2.setColor(UIManager.getColor(enabled ? "Component.borderColor" : "Component.disabledBorderColor"));
            FlatUIUtils.paintOutline(g2, 0, 0, w, h, t, arc);

            int aw = scale(arrowWidth);
            int ah = scale(arrowHeight);
            int ax = (w - aw) / 2;
            int ay = (h - ah) / 2;
            boolean up = getDirection() == SwingConstants.NORTH;
            if (enabled) {
                // light emboss one pixel to the right of the triangle
                g2.setColor(UIManager.getColor("Component.ringHighlightColor"));
                fillTriangle(g2, ax + scale(1), ay, aw, ah, up);
            }
            g2.setColor(enabled ? UIManager.getColor("ComboBox.buttonArrowColor")
                    : UIManager.getColor("ComboBox.buttonDisabledArrowColor"));
            fillTriangle(g2, ax, ay, aw, ah, up);
        } finally {
            g2.dispose();
        }
    }

    /** Row by row, without antialiasing: 7, 5, 3, 1 pixels for a 7x4 arrow. */
    private static void fillTriangle(Graphics2D g, int x, int y, int w, int h, boolean up) {
        for (int i = 0; i < h; i++) {
            int inset = Math.round(i * w / (2f * h));
            int row = up ? y + h - 1 - i : y + i;
            g.fillRect(x + inset, row, w - 2 * inset, 1);
        }
    }
}
