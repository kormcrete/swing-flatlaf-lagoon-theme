package io.github.kormcrete.flatlaf.lagoon;

import com.formdev.flatlaf.FlatLightLaf;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Headless: installing the look-and-feel needs no display. */
class FlatLagoonLafTest {

    @AfterEach
    void resetLookAndFeel() throws Exception {
        UIManager.put("defaultFont", null);
        UIManager.setLookAndFeel(new MetalLookAndFeel());
    }

    @Test
    void installsAsTheCurrentLookAndFeel() {
        assertTrue(FlatLagoonLaf.setup());
        assertEquals(FlatLagoonLaf.class, UIManager.getLookAndFeel().getClass());
        assertEquals("Lagoon", UIManager.getLookAndFeel().getName());
    }

    @Test
    void loadsItsPropertiesFile() {
        FlatLagoonLaf.setup();
        // A handful of keys that only the properties file sets; FlatLightLaf's own values differ.
        assertEquals(new Color(0xF5F6F7), UIManager.getColor("Panel.background"));
        assertEquals(new Color(0x5F6265), UIManager.getColor("Component.borderColor"));
        assertEquals(2, UIManager.getInt("Component.arc"));
        assertEquals("card", UIManager.getString("TabbedPane.tabType"));
        assertEquals(15, UIManager.getInt("ScrollBar.width"));
        assertEquals(new Insets(1, 1, 1, 1), UIManager.getInsets("Table.cellMargins"));
    }

    @Test
    void registersItsOwnDelegatesAndBorders() {
        FlatLagoonLaf.setup();
        assertEquals(LagoonTabbedPaneUI.class.getName(), UIManager.getString("TabbedPaneUI"));
        // Named by class in the properties file: a misspelt name only logs, so check each one resolved.
        assertEquals(LagoonBorders.Text.class, UIManager.getBorder("TextField.border").getClass());
        assertEquals(LagoonBorders.Text.class, UIManager.getBorder("PasswordField.border").getClass());
        assertEquals(LagoonBorders.Round.class, UIManager.getBorder("ComboBox.border").getClass());
        assertEquals(LagoonBorders.Round.class, UIManager.getBorder("Spinner.border").getClass());
        assertEquals(LagoonBorders.Button.class, UIManager.getBorder("Button.border").getClass());
        assertEquals(LagoonBorders.Button.class, UIManager.getBorder("ToggleButton.border").getClass());
        assertEquals(LagoonBorders.ScrollPane.class, UIManager.getBorder("ScrollPane.border").getClass());
        assertEquals(LagoonComboBoxUI.class.getName(), UIManager.getString("ComboBoxUI"));
        assertEquals(LagoonSpinnerUI.class.getName(), UIManager.getString("SpinnerUI"));
        assertEquals(LagoonButtonUI.class.getName(), UIManager.getString("ButtonUI"));
        Border titled = UIManager.getBorder("TitledBorder.border");
        assertEquals(new Insets(2, 2, 2, 2), titled.getBorderInsets(new JPanel()));
    }

    @Test
    void basesFontsOnDialog12WithBoldMenus() {
        FlatLagoonLaf.setup();
        Font label = UIManager.getFont("Label.font");
        assertEquals("Dialog", label.getName());
        assertEquals(Font.PLAIN, label.getStyle());
        assertEquals(Font.BOLD, UIManager.getFont("MenuBar.font").getStyle());
    }

    @Test
    void leavesAnApplicationsOwnBaseFontAlone() {
        UIManager.put("defaultFont", new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        FlatLagoonLaf.setup();
        Font label = UIManager.getFont("Label.font");
        assertEquals(Font.SANS_SERIF, label.getName());
    }

    /**
     * Every FlatLaf theme puts a {@code defaultFont} into its own defaults, which
     * {@code UIManager.get} also reads: that one is the previous theme's, not the application's.
     */
    @Test
    void switchingFromAnotherFlatLafThemeStillBasesFontsOnDialog12() {
        FlatLightLaf.setup();
        FlatLagoonLaf.setup();
        assertEquals("Dialog", UIManager.getFont("Label.font").getName());
    }

    @Test
    void labelsKeepTheirIconTextGapAfterTheText() {
        FlatLagoonLaf.setup();
        JLabel label = new JLabel("Username");
        int text = label.getFontMetrics(label.getFont()).stringWidth("Username");
        assertEquals(text + label.getIconTextGap(), label.getPreferredSize().width);
        assertEquals(text, new JLabel("<html>Username</html>").getPreferredSize().width);
        // Swing derives the minimum from the preferred size; the gap must not be added twice.
        assertEquals(label.getPreferredSize().width, label.getMinimumSize().width);
        // A list renderer is a label too, but its size is a list item's or a combo box value's.
        javax.swing.DefaultListCellRenderer renderer = new javax.swing.DefaultListCellRenderer();
        renderer.setText("Username");
        assertEquals(text + renderer.getInsets().left + renderer.getInsets().right, renderer.getPreferredSize().width);
        // A table cell keeps the gap, so a column fitted to its widest cell leaves the same room as a label.
        javax.swing.table.DefaultTableCellRenderer cell = new javax.swing.table.DefaultTableCellRenderer();
        cell.setText("Username");
        assertEquals(text + cell.getIconTextGap() + cell.getInsets().left + cell.getInsets().right,
                cell.getPreferredSize().width);
    }

    @Test
    void buttonsKeepTheGapAfterTheTextAndAnOddSize() {
        FlatLagoonLaf.setup();
        JButton save = new JButton("Speichern");
        int text = save.getFontMetrics(save.getFont()).stringWidth("Speichern");
        java.awt.Insets in = save.getInsets();
        assertEquals((text + save.getIconTextGap() + in.left + in.right) | 1, save.getPreferredSize().width);
        assertEquals(1, save.getPreferredSize().height % 2);
    }

    /** Field, combo, spinner and button heights the layout of a form is built on. */
    @Test
    void sizesControlsForADenseForm() {
        FlatLagoonLaf.setup();
        assertEquals(21, new JTextField(10).getPreferredSize().height);
        assertEquals(21, new JPasswordField(10).getPreferredSize().height);
        assertEquals(23, new JComboBox<>(new String[]{"x"}).getPreferredSize().height);
        assertEquals(24, new JSpinner().getPreferredSize().height);
        assertEquals(47, new JSpinner(new javax.swing.SpinnerNumberModel(12, 0, 99, 1)).getPreferredSize().width);
        assertEquals(25, new JButton("Save").getPreferredSize().height);
        assertEquals(23, new JCheckBox("x").getPreferredSize().height);
    }

    @Test
    void keepsOneSelectionColourWithAndWithoutFocus() {
        FlatLagoonLaf.setup();
        assertEquals(UIManager.getColor("Table.selectionBackground"),
                UIManager.getColor("Table.selectionInactiveBackground"));
        assertEquals(UIManager.getColor("List.selectionBackground"),
                UIManager.getColor("List.selectionInactiveBackground"));
    }

    /** The frame round a tab's content is one line, with room inside it on the three sides away from the tabs. */
    @Test
    void tabContentSitsInsideItsFrameWithRoomToSpare() {
        FlatLagoonLaf.setup();
        javax.swing.JTabbedPane tabs = new javax.swing.JTabbedPane();
        JPanel content = new JPanel();
        tabs.addTab("Orders", content);
        tabs.setSize(300, 200);
        tabs.doLayout();
        assertEquals(3, content.getX());
        // One line and one pixel of room below the tab.
        assertEquals(tabs.getBoundsAt(0).y + tabs.getBoundsAt(0).height + 2, content.getY());
        assertEquals(300 - 3, content.getX() + content.getWidth());
        assertEquals(200 - 3, content.getY() + content.getHeight());
        assertEquals(1, UIManager.getInt("TabbedPane.contentSeparatorHeight"));
    }

    @Test
    void aToolbarSeparatorKeepsItsLineInAFlowLayoutAndNoSizeOutlivesASwitch() {
        FlatLagoonLaf.setup();
        JToolBar bar = new JToolBar();
        bar.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 1));
        bar.addSeparator();
        assertEquals(new Dimension(16, 12), bar.getComponent(0).getPreferredSize());

        FlatLightLaf.setup();
        SwingUtilities.updateComponentTreeUI(bar);
        assertEquals(0, bar.getComponent(0).getPreferredSize().height);
    }
}
