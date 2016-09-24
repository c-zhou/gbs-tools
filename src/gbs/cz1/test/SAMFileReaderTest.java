package gbs.cz1.test;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.samtools.SAMFileHeader;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMProgramRecord;
import net.sf.samtools.SAMReadGroupRecord;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMRecordIterator;
import net.sf.samtools.SAMSequenceDictionary;
import net.sf.samtools.SAMSequenceRecord;
import net.sf.samtools.SAMFileReader.ValidationStringency;

public class SAMFileReaderTest {

	public static void main(String[] args) {
		final SAMFileReader inputSam = new SAMFileReader(new File(args[0]));
		inputSam.setValidationStringency(ValidationStringency.SILENT);
		final SAMFileHeader header = inputSam.getFileHeader();
		System.out.println(header.getSequence(0).getSequenceName());
		SAMRecordIterator iter=inputSam.iterator();
		SAMRecord rec = null;
		int i=0;
		try {
			while(iter.hasNext()) {
				rec = iter.next();
				i++;
				if(i==61354) 
					System.err.println(i);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			System.err.println(rec.getReferenceName());
			System.err.println(rec.getReadName());
			iter.close();
			inputSam.close();
			throw new RuntimeException("!!!");
		}
		iter.close();
		inputSam.close();
		/**
		final SAMFileReader inputSam = new SAMFileReader(new File(args[0]));
		final SAMFileHeader header = inputSam.getFileHeader();
		System.out.println(header.getProgramRecords().get(0).getCommandLine());
		System.out.println(header.getAttribute("VN"));
		System.out.println(header.getAttribute("SO"));
		//header.addProgramRecord(header.getProgramRecords().get(0));
		SAMSequenceDictionary seqdic = header.getSequenceDictionary();
		
		final Set<String> extract = new HashSet<String>();
		extract.add("scaffold1|size1187051");
		extract.add("scaffold10|size816804");
		extract.add("scaffold18|size671005");
		final SAMFileHeader header_new = new SAMFileHeader();
		header_new.setAttribute("VN", header.getAttribute("VN"));
		header_new.setAttribute("SO", header.getAttribute("SO"));
		final SAMSequenceDictionary seqdic_new = new SAMSequenceDictionary();
		List<SAMSequenceRecord> seqs = seqdic.getSequences();
		for(SAMSequenceRecord seq : seqs)
			if(extract.contains(seq.getSequenceName()))
				seqdic_new.addSequence(seq);
		for(SAMReadGroupRecord rg : header.getReadGroups())
			header_new.addReadGroup(rg);
		for(SAMProgramRecord pg : header.getProgramRecords())
			header_new.addProgramRecord(pg);
		**/
		
		
	}
}
