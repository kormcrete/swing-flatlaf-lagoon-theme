package io.github.kormcrete.flatlaf.lagoon;

import com.formdev.flatlaf.ui.FlatTableUI;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;

/**
 * Tables whose Boolean cells show the theme's {@link LagoonCheckBoxIcon}. FlatLaf gives a table a renderer of its
 * own for Boolean cells, drawn with FlatLaf's larger check box whatever {@code CheckBox.icon} says; this one takes
 * its place, and hands FlatLaf's back when the table is uninstalled, so FlatLaf restores the table's earlier one.
 */
public class LagoonTableUI extends FlatTableUI {

    private TableCellRenderer flatBooleanRenderer;

    public static ComponentUI createUI(JComponent c) {
        return new LagoonTableUI();
    }

    @Override
    protected void installDefaults() {
        super.installDefaults();
        TableCellRenderer installed = table.getDefaultRenderer(Boolean.class);
        // Only the renderer FlatLaf put there: one the application set is not a UIResource and stays.
        if (installed instanceof UIResource && !(installed instanceof BooleanRenderer)) {
            flatBooleanRenderer = installed;
            table.setDefaultRenderer(Boolean.class, new BooleanRenderer());
        }
    }

    @Override
    protected void uninstallDefaults() {
        if (flatBooleanRenderer != null && table.getDefaultRenderer(Boolean.class) instanceof BooleanRenderer) {
            table.setDefaultRenderer(Boolean.class, flatBooleanRenderer);
        }
        flatBooleanRenderer = null;
        super.uninstallDefaults();
    }

    private static final class BooleanRenderer extends DefaultTableCellRenderer implements UIResource {

        private boolean selected;

        BooleanRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
            Icon icon = new LagoonCheckBoxIcon() {
                @Override
                protected boolean isSelected(Component c) {
                    return selected;
                }
            };
            setIcon(icon);
            setDisabledIcon(icon);
        }

        @Override
        protected void setValue(Object value) {
            selected = Boolean.TRUE.equals(value);
        }
    }
}
