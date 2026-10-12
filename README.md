# swing-flatlaf-lagoon-theme

[![build](https://github.com/kormcrete/swing-flatlaf-lagoon-theme/actions/workflows/build.yml/badge.svg)](https://github.com/kormcrete/swing-flatlaf-lagoon-theme/actions/workflows/build.yml)
[![license](https://img.shields.io/badge/license-Apache--2.0-blue)](LICENSE)

A classic light theme for [FlatLaf](https://www.formdev.com/flatlaf/).

A neutral grey palette, square corners, one-pixel dark frames, bevelled buttons, framed card tabs
on a framed content area, white tables with a grid and blue scroll bars with buttons: the dense,
clearly delimited look of a traditional desktop application, for forms and data-heavy screens —
with everything FlatLaf brings underneath: HiDPI scaling, native window decorations, SVG icons
and an actively maintained look-and-feel.

## Use

Not on Maven Central yet: build it with `mvn install`, then depend on the snapshot.

```xml
<dependency>
    <groupId>io.github.kormcrete</groupId>
    <artifactId>swing-flatlaf-lagoon-theme</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

FlatLaf comes in as a transitive dependency (3.7.1); declare your own FlatLaf dependency to pin
another version.

```java
import io.github.kormcrete.flatlaf.lagoon.FlatLagoonLaf;

FlatLagoonLaf.setup();   // before the first Swing component is created
```

### Fonts

The base font is Dialog 12, with bold menus. `setup()` puts it into
`UIManager` as `defaultFont`, from which FlatLaf derives every other font and which it still
DPI-scales on Windows and Linux. An application that wants another base font sets its own
`defaultFont` before calling `setup()`; the theme leaves it alone:

```java
UIManager.put("defaultFont", new Font(Font.SANS_SERIF, Font.PLAIN, 13));
FlatLagoonLaf.setup();
```

### Switching at runtime

Like any FlatLaf theme:

```java
FlatLagoonLaf.setup();
FlatLaf.updateUI();
```

## What is in it

- `FlatLagoonLaf` — a `FlatLightLaf` subclass; FlatLaf loads `FlatLagoonLaf.properties` from the
  same package. The properties file is the one place that names colours and sizes, and is
  commented section by section.
- `LagoonTabbedPaneUI` — FlatLaf's card tabs frame only the selected tab; this theme frames and
  shades the unselected ones too. Its two colours are properties keys
  (`TabbedPane.unselectedBorderColor`, `TabbedPane.unselectedBottomColor`), and it handles all
  four tab placements.

Details a FlatLaf properties file cannot express, and that may follow as further UI delegates:
a two-band reflection on buttons, combo arrows and table headers, and a gradient on the menubar,
toolbar and scroll thumb.

## Notes for applications

- Cell renderers that keep Swing's static `DefaultTableCellRenderer.noFocusBorder` and take the
  focus border from the look-and-feel rely on both having the same insets. The theme keeps
  `Table.cellMargins = 1,1,1,1` for that reason; better still, read `Table.cellNoFocusBorder`
  from `UIManager`.
- FlatLaf honours `apple.laf.useScreenMenuBar` on macOS, which some other look-and-feels ignore.
  If your menu bar moves to the top of the screen after switching, that property is why.
- On Java 24 and later, FlatLaf's native library (window decorations and other platform
  integration) prints a "restricted method" warning when it loads; a future Java will block it.
  Start the application with `--enable-native-access=ALL-UNNAMED`, or add
  `Enable-Native-Access: ALL-UNNAMED` to an executable jar's manifest.

## Build

```
mvn package
```

Java 8 or later; CI runs the tests on Java 8 and 25, on Linux, macOS and Windows. Tests are headless. `LagoonGallery` (under `src/test`) opens a window with one
of everything, for a visual check:

```
mvn -q test-compile exec:java -Dexec.classpathScope=test -Dexec.mainClass=io.github.kormcrete.flatlaf.lagoon.LagoonGallery
```

## Licence

Apache License 2.0 — see `LICENSE` and `NOTICE`.
