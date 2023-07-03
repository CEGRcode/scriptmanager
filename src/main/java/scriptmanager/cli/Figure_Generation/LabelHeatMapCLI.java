package scriptmanager.cli.Figure_Generation;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import scriptmanager.objects.ToolDescriptions;
import scriptmanager.objects.CustomExceptions.OptionException;

import scriptmanager.scripts.Figure_Generation.LabelHeatMap;

/**
 * Command line interface class for labeling png heat maps, outputting an svg
 * 
 * @author Olivia Lang
 * @see scriptmanager.scripts.Figure_Generation.LabelHeatMap
 */
@Command(name = "label-heatmap", mixinStandardHelpOptions = true,
	description = ToolDescriptions.label_heatmap_description,
	version = "ScriptManager "+ ToolDescriptions.VERSION,
	sortOptions = false,
	exitCodeOnInvalidInput = 1,
	exitCodeOnExecutionException = 1)
public class LabelHeatMapCLI implements Callable<Integer> {
	// index 2 after two subcommands
	@Parameters(index = "0", description = "Input image (required)")
	private static File PNG;

	@Option(names = { "-o","--output" }, description = "Output image (default=\"OutputHeatmap.svg\"")
	private static File OUTPUT = new File("OutputHeatmap.svg");
	
	@Option(names = { "-c", "--color" }, description = "Output color: Must be in Hex format. (default=black)")
	private static String color = "000000"; // Color.BLACK
	@Option(names = { "-w", "--width" }, description = "Line thickness of border. Integer required. (default = 2)")
	private static Integer borderWidth = 2; // Set thickness of border and tickmarks
	@Option(names = { "-t", "--tick-height" }, description = "X-tickmark height. (default=10)")
	private static Integer xTickHeight = 10; // Height of X-axis tickmarks
	@Option(names = { "-f", "--font-size" }, description = "Font size. (default=18)")
	private static Integer FONTSIZE = 18; // Set font size

	@Option(names = { "-l", "--left-label" }, description = "Left X-tick label. (default=nolabel)")
	private static String xLeftLabel = ""; // Left X-tickmark label
	@Option(names = { "-m", "--mid-label" }, description = "Mid X-tick label. (default=nolabel)")
	private static String xMidLabel = ""; // Mid X-tickmark label
	@Option(names = { "-r", "--right-label" }, description = "Right X-tick label. (default=nolabel)")
	private static String xRightLabel = ""; // Right X-tickmark label

	@Option(names = { "-x", "--x-label" }, description = "X-axis label. (default=nolabel)")
	private static String xLabel = ""; // X-axis label
	@Option(names = { "-y", "--y-label" }, description = "Y-axis label. (default=nolabel)")
	private static String yLabel = ""; // Y-axis label

	public Integer call() throws IOException  {
		System.err.println(">LabelHeatMapCLI.call()");
		String validate = validateInput();
		if (!validate.equals("")) {
			System.err.println(validate);
			System.err.println("Invalid input. Check usage using '-h' or '--help'");
			System.exit(1);
		}
		try {
			// Generate HeatMap Label
			LabelHeatMap script_obj = new LabelHeatMap(PNG, OUTPUT, Color.decode(color),
					borderWidth, xTickHeight, FONTSIZE,
					xLeftLabel, xMidLabel, xRightLabel,
					xLabel, yLabel, null);
			script_obj.run();
			System.err.println("Label Generated.");
		} catch (OptionException e) {
			System.err.println(e.getMessage());
			System.err.println("Invalid input. Check usage using '-h' or '--help'");
			System.exit(1);
		}
		return (0);
	}
	
	private String validateInput() throws IOException {
		String r = "";
		// check inputs exist
		if (!PNG.exists()) {
			r += "(!)PNG input file does not exist: " + PNG.getName() + "\n";
			return (r);
		}
		// check output parent directory
		if (OUTPUT.getParent()!=null) {
			if (!new File(OUTPUT.getParent()).exists()) {
				r += "(!)Check output filepath is valid: " + OUTPUT.getAbsolutePath() + "\n";
			}
		}
		// check that hex string is formatted properly
		Pattern hexColorPat = Pattern.compile("[0-9A-Fa-f]{6}");
		Matcher m = hexColorPat.matcher(color);
		if (!m.matches()) {
			r += "(!)Color must be formatted as a hexidecimal String!\n\tExpected input string format: \"[0-9A-Fa-f]{6}\"\n";
		} else {
			color = "0x" + color;
		}
		
		// check numeric inputs are valid
		if (borderWidth < 0) {
			r += "(!) Invalid border width. Must be greater than or equal to 0\n";
		}
		if (xTickHeight < 0) {
			r += "(!) Invalid tick height (x-axis). Must be greater than or equal to 0\n";
		}
		if (FONTSIZE < 0) {
			r += "(!) Invalid font size. Must be greater than or equal to 0\n";
		}
		return(r);
	}
}

