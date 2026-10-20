package io.github.kormcrete.flatlaf.lagoon;

import javax.swing.ButtonModel;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import static com.formdev.flatlaf.util.UIScale.scale;

/**
 * The raised sheen of buttons, arrow buttons and table headers: a bright top, a slightly darker
 * band from about a quarter to a little past half the height, and a light lower part. A pressed face is
 * darker throughout, a hovered one brighter and lined in blue inside its top and bottom edges.
 *
 * <p>The stops are fractions of the face's height, so the same face fits a 9px spinner button and a
 * 25px button; they are the one place outside the properties file that names colours.
 */
final class LagoonSheen {

    enum State { NORMAL, ROLLOVER, PRESSED }

    private static final float[] NORMAL_STOPS = {0f, .125f, .175f, .2f, .55f, .575f, .75f, 1f};
    private static final Color[] NORMAL = colors(0xF4F7F9, 0xFFFFFF, 0xF4F5F7, 0xE8EBEF, 0xE5E9ED, 0xE9ECF0, 0xFAFBFC, 0xFAFBFC);

    private static final float[] ROLLOVER_STOPS = {0f, .2f, .22f, .5f, .52f, .75f, 1f};
    private static final Color[] ROLLOVER = colors(0xFFFFFF, 0xFFFFFF, 0xEBEFF3, 0xE9ECF0, 0xECEFF2, 0xFBFBFC, 0xFBFBFC);

    private static final float[] PRESSED_STOPS = {0f, .05f, .1f, .25f, .65f, .68f, .9f, 1f};
    private static final Color[] PRESSED = colors(0xEDEFF1, 0xFFFFFF, 0xE2E6EA, 0xDCE0E4, 0xDCE0E4, 0xD9DDE1, 0xF8F9FA, 0xF8F9FA);

    private static final Color TOP_HIGHLIGHT = new Color(0xEFF3F6);
    private static final Color LEFT_HIGHLIGHT = new Color(0xF0F3F5);
    private static final Color RIGHT_SHADOW = new Color(0xC7CBCE);
    private static final Color PRESSED_TOP = new Color(0xDBDFE3);
    private static final Color HOVER_LINE = new Color(0x96CEFF);

    private LagoonSheen() {
    }

    static State state(ButtonModel model, boolean selected) {
        if ((model.isArmed() && model.isPressed()) || selected) {
            return State.PRESSED;
        }
        return model.isRollover() ? State.ROLLOVER : State.NORMAL;
    }

    /**
     * Fills {@code shape} with the face, and lines the inside of the frame. {@code x, y, w, h} is the
     * area inside the frame line; {@code shape} may be larger (the frame is painted over its edge).
     */
    static void paint(Graphics2D g, Shape shape, float x, float y, float w, float h, State state) {
        float[] stops = state == State.PRESSED ? PRESSED_STOPS : state == State.ROLLOVER ? ROLLOVER_STOPS : NORMAL_STOPS;
        Color[] colors = state == State.PRESSED ? PRESSED : state == State.ROLLOVER ? ROLLOVER : NORMAL;
        if (h < 2) {
            g.setColor(colors[0]);
            g.fill(shape);
            return;
        }
        g.setPaint(new LinearGradientPaint(0, y, 0, y + h, stops, colors));
        g.fill(shape);

        float t = scale(1f);
        switch (state) {
            case PRESSED:
                g.setColor(PRESSED_TOP);
                g.fill(new Rectangle2D.Float(x, y, w, t));
                break;
            case ROLLOVER:
                g.setColor(HOVER_LINE);
                g.fill(new Rectangle2D.Float(x, y, w, t));
                g.fill(new Rectangle2D.Float(x, y + h - t, w, t));
                break;
            default:
                g.setColor(TOP_HIGHLIGHT);
                g.fill(new Rectangle2D.Float(x, y, w, t));
                g.setColor(LEFT_HIGHLIGHT);
                g.fill(new Rectangle2D.Float(x, y + t, t, h - t));
                g.setColor(RIGHT_SHADOW);
                g.fill(new Rectangle2D.Float(x + w - t, y + t, t, h - t));
        }
    }

    private static Color[] colors(int... rgb) {
        Color[] c = new Color[rgb.length];
        for (int i = 0; i < rgb.length; i++) {
            c[i] = new Color(rgb[i]);
        }
        return c;
    }
}
