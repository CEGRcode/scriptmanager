package cli.Figure_Generation;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.lang.NullPointerException;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import objects.ToolDescriptions;
import util.ExtensionFileFilter;
import scripts.Figure_Generation.MergeHeatMapPlot;

/**
 * Figure_GenerationCLI/MergeHeatMapCLI
 */
@Command(name = "merge-heatmap", mixinStandardHelpOptions = true, description = ToolDescriptions.merge_heatmap_description, sortOptions = false, exitCodeOnInvalidInput = 1, exitCodeOnExecutionException = 1)
public class MergeHeatMapCLI implements Callable<Integer> {

	@Parameters(index = "0", description = "First(sense) PNG heatmap to merge, input1")
	private File senseFile;
	@Parameters(index = "1", description = "Second(anti) PNG heatmap to merge, input2")
	private File antiFile;

	@Option(names = { "-o",
			"--output" }, description = "specify output filename, please use PNG extension\n(default=\"<senseFile>_merged.png\" appended to the name in working directory of ScriptManager")
	private File output = null;

	@Override
	public Integer call() throws Exception {
		System.err.println(">MergeHeatMapCLI.call()");
		String validate = validateInput();
		if (!validate.equals("")) {
			System.err.println(validate);
			System.err.println("Invalid input. Check usage using '-h' or '--help'");
			System.exit(1);
		}

		// Generate Merged HeatMap
		MergeHeatMapPlot.mergePNG(senseFile, antiFile, output);

		System.err.println("Image Generated.");
		return (0);
	}

	private String validateInput() throws IOException {
		String r = "";

		// check inputs exist
		if (!senseFile.exists()) {
			r += "(!)INPUT1 file does not exist: " + senseFile.getName() + "\n";
		}
		if (!antiFile.exists()) {
			r += "(!)INPUT2 file does not exist: " + antiFile.getName() + "\n";
		}
		if (!r.equals("")) {
			return (r);
		}
		// check input extensions
		if (!"png".equals(ExtensionFileFilter.getExtension(senseFile))) {
			r += "(!)Is this a PNG file? Check extension: " + senseFile.getName() + "\n";
		}
		if (!"png".equals(ExtensionFileFilter.getExtension(antiFile))) {
			r += "(!)Is this a PNG file? Check extension: " + antiFile.getName() + "\n";
		}
		// set default output filename
		if (output == null) {
			String NAME = ExtensionFileFilter.stripExtension(senseFile);
			output = new File(NAME + "_merged.png");
			// check output filename is valid
		} else {
			// check ext
			try {
				if (!"png".equals(ExtensionFileFilter.getExtension(output))) {
					r += "(!)Use PNG extension for output filename. Try: " + ExtensionFileFilter.stripExtension(output)
							+ ".png\n";
				}
			} catch (NullPointerException e) {
				r += "(!)Output filename must have extension: use PNG extension for output filename. Try: " + output
						+ ".png\n";
			}
			// check directory
			if (output.getParent() == null) {
// 				System.err.println("default to current directory");
			} else if (!new File(output.getParent()).exists()) {
				r += "(!)Check output directory exists: " + output.getParent() + "\n";
			}
		}

		return (r);
	}

}