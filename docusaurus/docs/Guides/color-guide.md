---
id: color-guide
title: Color Selection Guide
sidebar_label: Color Selection Guide
---

Some tools allow you to customize colors used in the output, specifically among the `figure-generation` tools. The following guide introduces the user to color customization in ScriptManager.

Color customization options available in the [Heatmap Labeler][heatmap-labeler], [Four Color Sequence Plot][four-color], [Two Color Heatmap][heatmap], [Three Color Heatmap][three-color-heatmap], and [Composite Plot][composite] tools.


## Color Selector Window (GUI)

When the user opens up the color selector window, they will see several tabs, each visualizing a different method for selecting a custom color.

![swatch-guide](/../static/md-img/ColorSelector/swatch-guide.png)

The default/first tab, "Swatch" (above), shows a bunch of color swatches for a fixed collection of colors to choose from.

* "HSV" tab allows the user to select a color based on the Hue, Saturation, and Value color system
* "HSL" tab  allows the user to select a color based on the Hue, Saturation, and Lightness color system
* "RGB" tab  allows the user to select a color based on the Red, Green, and Blue color system
* "CMYK" tab  allows the user to select a color based on the Cyan, Magenta, Yellow, and Black color system

<img src={require('/../static/md-img/ColorSelector/hsv-guide.png').default} style={{width:50+'%'}}/><img src={require('/../static/md-img/ColorSelector/hsl-guide.png').default} style={{width:50+'%'}}/>
<img src={require('/../static/md-img/ColorSelector/rgb-guide.png').default} style={{width:50+'%'}}/><img src={require('/../static/md-img/ColorSelector/cmyk-guide.png').default} style={{width:50+'%'}}/>

Note that the RGB selector window includes a "Color Code" text box that allows you to directly paste a hexstring (see below) value encoding a color.

## Choosing colors from the command line (CLI)

ScriptManager's command-line tools typically indicate color using the `-c` flag followed one or more hexidecimal color strings shexstrings). The hexstrings are composed of a sequence of 6 characters (0-9 or A-F), where each pair of characters represent an Red, Green, and Blue value, and each pair encoding any value from 0-255. The help documentation points the user to [this url][color-hex-url] for users to browse colors and get the corresponding hexstring.

$$
\huge{
  \#\textcolor{red}{FF}\textcolor{green}{B4}\space\textcolor{blue}{00}
}
$$
:::caution

User should not use the pound symbol `#` in front of the hexidecimal because it renders the token invisible to bash and thus, ScriptManager.

:::


[color-hex-url]:http://www.javascripter.net/faq/rgbtohex.htm

[four-color]:/docs/figure-generation/heatmap
[heatmap]:/docs/figure-generation/Four-color
[three-color-heatmap]:/docs/figure-generation/three-color-heatmap
[heatmap-labeler]:/docs/figure-generation/heatmap-labeler
[composite]:/docs/figure-generation/composite-plot
