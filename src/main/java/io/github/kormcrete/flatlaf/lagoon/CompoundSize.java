package io.github.kormcrete.flatlaf.lagoon;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicHTML;
import java.awt.Dimension;

/**
 * The theme's sizing of a label or button that has text, an icon or both, applied on top of the size
 * Swing's compound-label layout gives.
 *
 * <ul>
 *   <li>Text beside an icon, or text alone: the icon-text gap is reserved after the text even when
 *       there is no icon, so a column of labels leaves a little air before the fields next to it.</li>
 *   <li>Text above or below an icon: the gap is reserved twice, once between them and once after.</li>
 *   <li>Buttons are given an odd width and height, so their text and icon centre on a whole pixel.</li>
 * </ul>
 *
 * HTML text is sized as FlatLaf sizes it.
 */
final class CompoundSize {

    private CompoundSize() {
    }

    static Dimension adjust(JComponent c, Dimension size, String text, Icon icon, int gap,
                            int horizontalTextPosition, int verticalTextPosition) {
        if (size == null || c.getClientProperty(BasicHTML.propertyKey) != null) {
            return size;
        }
        boolean hasText = text != null && !text.isEmpty();
        if (horizontalTextPosition != SwingConstants.CENTER) {
            if (hasText && icon == null) {
                size.width += gap;
            }
        } else if (verticalTextPosition != SwingConstants.CENTER) {
            size.height += gap;
        }
        return size;
    }

    static Dimension odd(Dimension size) {
        if (size != null) {
            size.width |= 1;
            size.height |= 1;
        }
        return size;
    }
}
