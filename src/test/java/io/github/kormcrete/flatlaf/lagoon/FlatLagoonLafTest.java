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
import javax.swing.JScrollBar;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.image.BufferedImage;

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
        assertEquals(16, UIManager.getInt("ScrollBar.width"));
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
        // HTML text takes the same room as plain text: a centred label must not shift when it turns into a link.
        assertEquals(text + label.getIconTextGap(), new JLabel("<html>Username</html>").getPreferredSize().width);
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
    void checkBoxesAndRadioButtonsHaveTwelvePixelIconsWhoseFrameFillsThem() {
        FlatLagoonLaf.setup();
        javax.swing.Icon check = UIManager.getIcon("CheckBox.icon");
        javax.swing.Icon radio = UIManager.getIcon("RadioButton.icon");
        assertEquals(new Dimension(12, 12), new Dimension(check.getIconWidth(), check.getIconHeight()));
        assertEquals(new Dimension(12, 12), new Dimension(radio.getIconWidth(), radio.getIconHeight()));
        JCheckBox box = new JCheckBox("Print", true);
        BufferedImage image = new BufferedImage(12, 12, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        check.paintIcon(box, g, 0, 0);
        g.dispose();
        int frame = UIManager.getColor("CheckBox.icon.selectedBorderColor").getRGB();
        assertEquals(frame, image.getRGB(0, 6));
        assertEquals(frame, image.getRGB(11, 6));
        assertEquals(frame, image.getRGB(6, 0));
        assertEquals(frame, image.getRGB(6, 11));
    }

    @Test
    void aTablesBooleanCellsShowTheThemesCheckBoxAndGiveTheRendererBackOnASwitch() throws Exception {
        FlatLagoonLaf.setup();
        JTable table = new JTable(new Object[][] {{true}, {false}}, new Object[] {"Done"}) {
            @Override
            public Class<?> getColumnClass(int column) {
                return Boolean.class;
            }
        };
        javax.swing.table.TableCellRenderer renderer = table.getDefaultRenderer(Boolean.class);
        JLabel on = (JLabel) renderer.getTableCellRendererComponent(table, true, false, false, 0, 0);
        assertEquals(12, on.getIcon().getIconWidth());
        int[] checked = paint(on.getIcon(), on);
        JLabel off = (JLabel) renderer.getTableCellRendererComponent(table, false, false, false, 1, 0);
        assertTrue(!java.util.Arrays.equals(checked, paint(off.getIcon(), off)), "a true and a false cell look alike");

        UIManager.setLookAndFeel(new MetalLookAndFeel());
        SwingUtilities.updateComponentTreeUI(table);
        assertTrue(table.getDefaultRenderer(Boolean.class) instanceof JCheckBox,
                "the table kept " + table.getDefaultRenderer(Boolean.class));
    }

    private static int[] paint(javax.swing.Icon icon, java.awt.Component c) {
        BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        icon.paintIcon(c, g, 0, 0);
        g.dispose();
        return image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
    }

    @Test
    void buttonsKeepTheGapAfterTheTextAndAnOddSize() {
        FlatLagoonLaf.setup();
        JButton save = new JButton("Speichern");
        int text = save.getFontMetrics(save.getFont()).stringWidth("Speichern");
        java.awt.Insets in = save.getInsets();
        assertEquals((text + save.getIconTextGap() + in.left + in.right) | 1, save.getPreferredSize().width);
        assertEquals(1, save.getPreferredSize().height % 2);
        assertEquals(save.getPreferredSize(), new JButton("<html>Speichern</html>").getPreferredSize());
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

    @Test
    void contentIsFramedInATabsLineAndTextInTheFieldsFrame() {
        FlatLagoonLaf.setup();
        int tab = UIManager.getColor("TabbedPane.contentAreaColor").getRGB();
        int field = UIManager.getColor("Component.borderColor").getRGB();
        int[] table = frameLeftEdge(new JScrollPane(new JTable(3, 3)));
        assertEquals(0, table[0] >>> 24, "no ring around content");
        assertEquals(tab, table[1]);
        assertEquals(field, frameLeftEdge(new JScrollPane(new javax.swing.JTextArea(3, 10)))[1]);
    }

    @Test
    void aTableFillingATabKeepsItsOwnFrame() {
        FlatLagoonLaf.setup();
        JScrollPane scrollPane = new JScrollPane(new JTable(3, 3));
        javax.swing.JTabbedPane tabs = new javax.swing.JTabbedPane();
        tabs.addTab("One", scrollPane);
        tabs.setSize(300, 200);
        tabs.doLayout();
        assertEquals(UIManager.getColor("TabbedPane.contentAreaColor").getRGB(), frameLeftEdge(scrollPane)[1]);
    }

    /** The first pixels of the middle row of a scroll pane's border, painted on its own. */
    private static int[] frameLeftEdge(JScrollPane scrollPane) {
        if (scrollPane.getWidth() == 0) {
            scrollPane.setSize(200, 100);
        }
        BufferedImage image = new BufferedImage(scrollPane.getWidth(), scrollPane.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        scrollPane.getBorder().paintBorder(scrollPane, g, 0, 0, image.getWidth(), image.getHeight());
        g.dispose();
        int[] edge = new int[3];
        for (int x = 0; x < edge.length; x++) {
            edge[x] = image.getRGB(x, image.getHeight() / 2);
        }
        return edge;
    }

    @Test
    void aTablesScrollPaneKeepsTheThemesFrameSoFocusPaintsNoRing() {
        FlatLagoonLaf.setup();
        JTable table = new JTable(3, 3) {
            {
                // What JTable does on addNotify, which a headless test never reaches.
                new JScrollPane(this);
                configureEnclosingScrollPane();
            }
        };
        assertEquals(LagoonBorders.ScrollPane.class, ((JScrollPane) table.getParent().getParent()).getBorder().getClass());
    }

    @Test
    void aLongThumbCarriesThreeRidgesAcrossItsMiddleAndAShortOneNone() {
        FlatLagoonLaf.setup();
        int shadow = UIManager.getColor("ScrollBar.gripShadowColor").getRGB();
        // A thumb of 30 px along a 15 px track breadth: its centre column crosses every ridge once.
        assertEquals(3, runsOf(shadow, thumbCentreColumn(new JScrollBar(JScrollBar.VERTICAL, 0, 40, 0, 100))));
        // Too short to hold them with room to spare.
        assertEquals(0, runsOf(shadow, thumbCentreColumn(new JScrollBar(JScrollBar.VERTICAL, 0, 1, 0, 100))));
    }

    private static int[] thumbCentreColumn(JScrollBar bar) {
        bar.setSize(16, 100);
        bar.doLayout();
        BufferedImage image = new BufferedImage(bar.getWidth(), bar.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        bar.paint(g);
        g.dispose();
        int[] column = new int[image.getHeight()];
        for (int y = 0; y < column.length; y++) {
            column[y] = image.getRGB(1 + 15 / 2, y);
        }
        return column;
    }

    private static int runsOf(int rgb, int[] pixels) {
        int runs = 0;
        for (int i = 0; i < pixels.length; i++) {
            if (pixels[i] == rgb && (i == 0 || pixels[i - 1] != rgb)) {
                runs++;
            }
        }
        return runs;
    }

    @Test
    void aTablesVerticalScrollBarStartsBelowItsHeaderWithSquareButtonsInsideItsLine() {
        FlatLagoonLaf.setup();
        JTable table = new JTable(200, 3);
        JScrollPane scrollPane = new JScrollPane(table);
        // What JTable does on addNotify, which a headless test never reaches.
        scrollPane.setColumnHeaderView(table.getTableHeader());
        scrollPane.setSize(400, 300);
        scrollPane.doLayout();
        JScrollBar bar = scrollPane.getVerticalScrollBar();
        bar.doLayout();
        assertEquals(scrollPane.getColumnHeader().getY() + scrollPane.getColumnHeader().getHeight(), bar.getY());
        assertEquals(16, bar.getWidth());
        assertEquals(new Dimension(15, 15), bar.getComponent(0).getSize());
        assertEquals(1, bar.getComponent(0).getX());
    }

    @Test
    void htmlLinksInLabelsTakeTheThemesLinkColour() {
        FlatLagoonLaf.setup();
        javax.swing.text.html.StyleSheet css = new javax.swing.text.html.HTMLEditorKit().getStyleSheet();
        assertEquals(new Color(0x0033CC), css.getForeground(css.getRule("a")));
    }

    /** A theme that puts its own base font into UIManager and takes it back when it is switched away from. */
    public static final class FontOwningTheme extends FlatLightLaf {
        static final Font FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 14);

        @Override
        public void initialize() {
            UIManager.put("defaultFont", FONT);
            super.initialize();
        }

        @Override
        public void uninitialize() {
            UIManager.put("defaultFont", null);
            super.uninitialize();
        }
    }

    @Test
    void switchingFromAThemeThatTakesItsFontBackBasesFontsOnDialog12() throws Exception {
        UIManager.setLookAndFeel(new FontOwningTheme());
        assertEquals(Font.SANS_SERIF, UIManager.getFont("Label.font").getName());
        FlatLagoonLaf.setup();
        assertEquals("Dialog", UIManager.getFont("Label.font").getName());
    }

    @Test
    void switchingAwayTakesTheBaseFontBackOutOfUIManager() {
        FlatLagoonLaf.setup();
        FlatLightLaf.setup();
        // Nothing left above the new theme's own defaults: UIManager answers with the theme's font.
        assertTrue(UIManager.get("defaultFont") == UIManager.getLookAndFeelDefaults().get("defaultFont"));
    }
}
