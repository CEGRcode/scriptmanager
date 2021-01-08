package main;

import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

import cli.BAM_Format_Converter.BAMtoBEDCLI;
import cli.BAM_Format_Converter.BAMtobedGraphCLI;
import cli.BAM_Format_Converter.BAMtoGFFCLI;
import cli.BAM_Format_Converter.BAMtoscIDXCLI;

import cli.BAM_Manipulation.BAIIndexerCLI;
import cli.BAM_Manipulation.BAMRemoveDupCLI;
import cli.BAM_Manipulation.FilterforPIPseqCLI;
import cli.BAM_Manipulation.MergeBAMCLI;
import cli.BAM_Manipulation.SortBAMCLI;

import cli.BAM_Statistics.BAMGenomeCorrelationCLI;
import cli.BAM_Statistics.PEStatsCLI;
import cli.BAM_Statistics.SEStatsCLI;

import cli.Coordinate_Manipulation.BED_Manipulation.BEDtoGFFCLI;
import cli.Coordinate_Manipulation.BED_Manipulation.ExpandBEDCLI;
import cli.Coordinate_Manipulation.BED_Manipulation.SortBEDCLI;

import cli.Coordinate_Manipulation.GFF_Manipulation.ExpandGFFCLI;
import cli.Coordinate_Manipulation.GFF_Manipulation.GFFtoBEDCLI;
import cli.Coordinate_Manipulation.GFF_Manipulation.SortGFFCLI;

import cli.Figure_Generation.CompositePlotCLI;
import cli.Figure_Generation.FourColorSequenceCLI;
import cli.Figure_Generation.TwoColorHeatMapCLI;
import cli.Figure_Generation.ThreeColorHeatMapCLI;
import cli.Figure_Generation.MergeHeatMapCLI;

import cli.File_Utilities.MD5ChecksumCLI;

import cli.Peak_Analysis.BEDPeakAligntoRefCLI;
import cli.Peak_Analysis.FilterBEDbyProximityCLI;
import cli.Peak_Analysis.RandomCoordinateCLI;
import cli.Peak_Analysis.SignalDuplicationCLI;
import cli.Peak_Analysis.TileGenomeCLI;

import cli.Peak_Calling.GeneTrackCLI;
import cli.Peak_Calling.PeakPairCLI;

import cli.Read_Analysis.AggregateDataCLI;
import cli.Read_Analysis.ScaleMatrixCLI;
import cli.Read_Analysis.ScalingFactorCLI;
//import cli.Read_Analysis.SimilarsityMatrixCLI;
import cli.Read_Analysis.TagPileupCLI;

import cli.Sequence_Analysis.DNAShapefromBEDCLI;
import cli.Sequence_Analysis.DNAShapefromFASTACLI;
import cli.Sequence_Analysis.FASTAExtractCLI;
import cli.Sequence_Analysis.RandomizeFASTACLI;
import cli.Sequence_Analysis.SearchMotifCLI;


@Command(name = "script-manager",
		subcommands = {
			BAM_Format_ConverterCLI.class,
			BAM_ManipulationCLI.class,
			BAM_StatisticsCLI.class,
			Coordinate_ManipulationCLI.class,
			Figure_GenerationCLI.class,
			File_UtilitiesCLI.class,
			Peak_AnalysisCLI.class,
			Peak_CallingCLI.class,
			Read_AnalysisCLI.class,
			Sequence_AnalysisCLI.class
		},
		version = "ScriptManager-v0.13-dev",
		mixinStandardHelpOptions = true,
		description = "Choose a tool directory from below to see more command-line tool options.",
		exitCodeOnInvalidInput = 1,
		exitCodeOnExecutionException = 1)
public class ScriptManager implements Callable<Integer> {
	
	@Override
	public Integer call(){
		System.out.println( "Use '-h' or '--help' for command-line usage guide" );
		ScriptManagerGUI gui = new ScriptManagerGUI();
		gui.launchApplication();
		return(0);
	}
	
	public static void main(String[] args) {
		CommandLine cmd = new CommandLine( new ScriptManager() );
		int exitCode = cmd.execute(args);
		if(exitCode != 0) {
			System.out.println("main:Exit code = " + exitCode);
			System.exit(exitCode);
		}
	}
}


@Command(mixinStandardHelpOptions = true,
		exitCodeOnInvalidInput = 1,
		exitCodeOnExecutionException = 1)
abstract class SubcommandCLI implements Callable<Integer>{
	
	@Override
	public Integer call(){
		CommandLine cmd = new CommandLine( this );
		cmd.usage(System.out);
		return(1);
	}
}


@Command(name = "bam-format-converter",
		subcommands = {
			BAMtoBEDCLI.class,
			BAMtobedGraphCLI.class,
			BAMtoGFFCLI.class,
			BAMtoscIDXCLI.class
		},
		description = "Includes tools like BAMtoBEDCLI, BAMtobedGraphCLI, BAMtoGFFCLI, and BAMtoscIDXCLI.")
class BAM_Format_ConverterCLI extends SubcommandCLI {}


@Command(name = "bam-manipulation",
		subcommands = {
			BAIIndexerCLI.class,
			BAMRemoveDupCLI.class,
			FilterforPIPseqCLI.class,
			MergeBAMCLI.class,
			SortBAMCLI.class
		},
		description = "Includes tools like BAIIndexerCLI, BAMRemoveDupCLI, FilterforPIPseqCLI, MergeBAMCLI, and SortBAMCLI.")
class BAM_ManipulationCLI extends SubcommandCLI {}


@Command(name = "bam-statistics",
		subcommands = {
			BAMGenomeCorrelationCLI.class,
			PEStatsCLI.class,
			SEStatsCLI.class
		},
		description = "Includes tools like BAMGenomeCorrelationCLI, PEStatsCLI, and SEStatsCLI.")
class BAM_StatisticsCLI extends SubcommandCLI {}


@Command(name = "coordinate-manipulation",
		subcommands = {
			BEDtoGFFCLI.class,
			ExpandBEDCLI.class,
			SortBEDCLI.class,
			ExpandGFFCLI.class,
			GFFtoBEDCLI.class,
			SortGFFCLI.class
		},
		description = "Includes tools like BEDtoGFFCLI, ExpandBEDCLI, SortBEDCLI, ExpandGFFCLI, GFFtoBEDCLI, and SortGFFCLI.")
class Coordinate_ManipulationCLI extends SubcommandCLI {}


@Command(name = "figure-generation",
		subcommands = {
			CompositePlotCLI.class,
			FourColorSequenceCLI.class,
			TwoColorHeatMapCLI.class,
			ThreeColorHeatMapCLI.class,
			MergeHeatMapCLI.class
		},
		description = "Includes tools like FourColorSequenceCLI, HeatMapCLI, and MergeHeatMapCLI.")
class Figure_GenerationCLI extends SubcommandCLI {}


@Command(name = "file-utilities",
		subcommands = {
			MD5ChecksumCLI.class	
		},
		description = "Includes the tool MD5Checksum.")
class File_UtilitiesCLI extends SubcommandCLI {}


@Command(name = "peak-analysis",
		subcommands = {
			BEDPeakAligntoRefCLI.class,
			FilterBEDbyProximityCLI.class,
			RandomCoordinateCLI.class,
			SignalDuplicationCLI.class,
			TileGenomeCLI.class
		},
		description = "Includes tools like BEDPeakAligntoRefCLI, FilterBEDbyProximityCLI, RandomCoordinateCLI, SignalDuplicationCLI, and TileGenomeCLI.")
class Peak_AnalysisCLI extends SubcommandCLI {}


@Command(name = "peak-calling",
		subcommands = {
			GeneTrackCLI.class,
			PeakPairCLI.class
		},
		description = "Includes tools like GeneTrackCLI and PeakPairCLI.")
class Peak_CallingCLI extends SubcommandCLI {}


@Command(name = "read-analysis",
		subcommands = {
			AggregateDataCLI.class,
			ScaleMatrixCLI.class,
			ScalingFactorCLI.class,
// 			SimilarityMatrixCLI.class,
			TagPileupCLI.class
		},
		description = "Includes tools like AggregateDataCLI, ScaleMatrixCLI, ScalingFactorCLI, SimilarityMatrixCLI, and TagPileupCLI.")
class Read_AnalysisCLI extends SubcommandCLI {}


@Command(name = "sequence-analysis",
		subcommands = {
			DNAShapefromBEDCLI.class,
			DNAShapefromFASTACLI.class,
			FASTAExtractCLI.class,
			RandomizeFASTACLI.class,
			SearchMotifCLI.class
		},
		description = "Includes tools like DNAShapefromBEDCLI, DNAShapefromFASTACLI, FASTAExtractCLI, RandomizeFASTACLI, and SearchMotifCLI.")
class Sequence_AnalysisCLI extends SubcommandCLI {}
