package io.github.kormcrete.flatlaf.lagoon;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.BorderUIResource;
import java.awt.Color;
import java.awt.Font;

/**
 * A classic light theme for FlatLaf: neutral grey, square corners, one-pixel dark frames, framed
 * card tabs, white tables with a grid and blue scroll bars with buttons. Colours and sizes live in
 * {@code FlatLagoonLaf.properties}.
 *
 * <p>Install with {@link #setup()}, before the first Swing component is created.
 */
public class FlatLagoonLaf extends FlatLightLaf {

    private static final Font BASE_FONT = new Font(Font.DIALOG, Font.PLAIN, 12);

    public static final String NAME = "Lagoon";

    /**
     * Installs the theme with Dialog 12 as the base font, unless the application has already put
     * its own {@code defaultFont} into {@link UIManager}. The font is set here, not in the
     * properties file, so that FlatLaf still DPI-scales it.
     *
     * @return whether the look-and-feel was installed, as {@link FlatLaf#setup(javax.swing.LookAndFeel)}
     */
    public static boolean setup() {
        if (!applicationSetDefaultFont()) {
            UIManager.put("defaultFont", BASE_FONT);
        }
        return FlatLaf.setup(new FlatLagoonLaf());
    }

    /**
     * {@code UIManager.get} also reads the installed look-and-feel's defaults, and every FlatLaf theme
     * puts a {@code defaultFont} there; that one is the previous theme's, not the application's.
     */
    private static boolean applicationSetDefaultFont() {
        Object font = UIManager.get("defaultFont");
        return font != null && font != UIManager.getLookAndFeelDefaults().get("defaultFont");
    }

    @Override
    public UIDefaults getDefaults() {
        UIDefaults defaults = super.getDefaults();
        // Frames the unselected tabs too, which no key can express.
        defaults.put("TabbedPaneUI", LagoonTabbedPaneUI.class.getName());
        // Label and button sizes keep the icon-text gap after the text (CompoundSize).
        defaults.put("LabelUI", LagoonLabelUI.class.getName());
        defaults.put("ButtonUI", LagoonButtonUI.class.getName());
        defaults.put("ToggleButtonUI", LagoonButtonUI.Toggle.class.getName());
        // The arrows of combo boxes and spinners are small raised buttons inside the frame.
        defaults.put("ComboBoxUI", LagoonComboBoxUI.class.getName());
        defaults.put("SpinnerUI", LagoonSpinnerUI.class.getName());
        // Toolbar separators keep their line where a toolbar is laid out by preferred sizes.
        defaults.put("ToolBarSeparatorUI", LagoonToolBarSeparatorUI.class.getName());
        // Two-tone etched group border; a properties file cannot name a Border class.
        defaults.put("TitledBorder.border", new BorderUIResource.EtchedBorderUIResource(
                new Color(0xF7F8F8), new Color(0xBCBDBE)));
        return defaults;
    }

    @Override
    public String getName() { return NAME; }

    @Override
    public String getDescription() { return "Lagoon - a classic light theme for FlatLaf, with blue accents"; }
}
