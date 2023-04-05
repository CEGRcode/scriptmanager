package scriptmanager.scripts.BAM_Manipulation;

import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMFileWriter;
import htsjdk.samtools.SAMFileWriterFactory;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.util.IOUtil;

import java.io.File;

public class BAMFileSort {
	public static void sort(File INPUT, File OUTPUT) {
		IOUtil.assertFileIsReadable(INPUT);
        IOUtil.assertFileIsWritable(OUTPUT);
        final SamReader reader = SamReaderFactory.makeDefault().open(INPUT);
        reader.getFileHeader().setSortOrder(SAMFileHeader.SortOrder.coordinate);
        final SAMFileWriter writer = new SAMFileWriterFactory().makeSAMOrBAMWriter(reader.getFileHeader(), false, OUTPUT);
        for (final SAMRecord rec: reader) {
            writer.addAlignment(rec);
        }
        writer.close();
	}
}
