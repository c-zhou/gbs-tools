package gbs.cz1.test;

import java.io.File;

import net.sf.samtools.SAMFileHeader;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMFileReader.ValidationStringency;
import net.sf.samtools.SAMFileWriter;
import net.sf.samtools.SAMFileWriterFactory;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMRecordIterator;


public class PicardSamIOTest {

	public static void main(String[] args) {
		SAMFileReader inputSam = new SAMFileReader(new File(args[0]));
		
		final SAMFileHeader header = inputSam.getFileHeader();
		final SAMFileWriter outputSam = new SAMFileWriterFactory().
				makeSAMOrBAMWriter(header,
                true, new File(args[1]));
		
		inputSam.setValidationStringency(ValidationStringency.SILENT);
		SAMRecordIterator iter=inputSam.iterator();

		while(iter.hasNext())
		{
			SAMRecord rec=iter.next();
			if(rec.getReadUnmappedFlag()) continue;
			System.out.println(rec.getReferenceName());
			outputSam.addAlignment(rec);
		}
		iter.close();
		inputSam.close();
		outputSam.close();
	}
}
