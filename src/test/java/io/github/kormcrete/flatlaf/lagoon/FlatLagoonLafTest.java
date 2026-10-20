package io.github.kormcrete.flatlaf.lagoon;

import com.formdev.flatlaf.FlatLightLaf;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.Color;
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
        assertEquals(0, UIManager.getInt("Component.arc"));
        assertEquals("card", UIManager.getString("TabbedPane.tabType"));
        assertEquals(15, UIManager.getInt("ScrollBar.width"));
        assertEquals(new Insets(1, 1, 1, 1), UIManager.getInsets("Table.cellMargins"));
    }

    @Test
    void registersItsOwnDelegatesAndBorders() {
        FlatLagoonLaf.setup();
        assertEquals(LagoonTabbedPaneUI.class.getName(), UIManager.getString("TabbedPaneUI"));
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
    void keepsOneSelectionColourWithAndWithoutFocus() {
        FlatLagoonLaf.setup();
        assertEquals(UIManager.getColor("Table.selectionBackground"),
                UIManager.getColor("Table.selectionInactiveBackground"));
        assertEquals(UIManager.getColor("List.selectionBackground"),
                UIManager.getColor("List.selectionInactiveBackground"));
    }
}
