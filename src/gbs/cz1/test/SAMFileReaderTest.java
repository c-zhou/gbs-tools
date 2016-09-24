package gbs.cz1.test;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.samtools.SAMFileHeader;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMProgramRecord;
import net.sf.samtools.SAMReadGroupRecord;
import net.sf.samtools.SAMSequenceDictionary;
import net.sf.samtools.SAMSequenceRecord;

public class SAMFileReaderTest {

	public static void main(String[] args) {
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
		
		
		
	}
}
