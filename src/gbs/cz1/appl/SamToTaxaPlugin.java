package gbs.cz1.appl;

import gbs.cz1.util.ArgsEngine;
import gbs.cz1.util.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class SamToTaxaPlugin {
	private final static Logger myLogger = 
			Logger.getLogger(SamToTaxaPlugin.class);
	private ArgsEngine myArgsEngine = null;
	private String mySamFileName = null;
	private String myOutputDir = null;
	private String myIndexFileName = null;
	private boolean mySamIsSorted = false;
	private final static int mb = 1024*1024;
	private final static Runtime instance = Runtime.getRuntime();

	static {
		BasicConfigurator.configure();
	}

	public static void main(String[] args) {
		SamToTaxaPlugin ftt = new SamToTaxaPlugin();
		ftt.setParameters(args);
		ftt.distributeTags();
	}

	private void printUsage() {
		myLogger.info(
				"\n\nUsage is as follows:\n"
						+ " -s  Input sam file.\n\n"
						+ " -t  Threads (default is 1).\n\n"
						+ " -S	The sam file is sorted.\n\n" 
						+ " -o  Output directory to contain .cnt files (one per FASTQ file, defaults to input directory).\n\n"
						+ " -i  Tag index file.");
	}

	public void setParameters(String[] args) {
		if (args.length == 0) {
			printUsage();
			throw new IllegalArgumentException("\n\nPlease use the above arguments/options.\n\n");
		}

		if (myArgsEngine == null) {
			myArgsEngine = new ArgsEngine();
			myArgsEngine.add("-i", "--index-file", true);
			myArgsEngine.add("-t", "--threads", true);
			myArgsEngine.add("-o", "--output-file", true);
			myArgsEngine.add("-s", "--sam-file", true);
			myArgsEngine.add("-S", "--sorted", false);
			myArgsEngine.parse(args);
		}

		if (myArgsEngine.getBoolean("-s")) {
			mySamFileName = myArgsEngine.getString("-s");
		} else {
			printUsage();
			throw new IllegalArgumentException("Please specify the location of your FASTQ files.");
		}

		if (myArgsEngine.getBoolean("-S")) {
			mySamIsSorted = true;
		}

		if (myArgsEngine.getBoolean("-t")) {
			THREADS = Integer.parseInt(myArgsEngine.getString("-t"));
		}

		if (myArgsEngine.getBoolean("-i")) {
			myIndexFileName = myArgsEngine.getString("-i");
		}

		if (myArgsEngine.getBoolean("-o")) {
			myOutputDir = myArgsEngine.getString("-o");
		}

	}

	/**
	 * Derives a tagCount list for each fastq file in the fastqDirectory.
	 *
	 * @param keyFileS A key file (a sample key by barcode, with a plate map
	 * included).
	 * @param enzyme The enzyme used to create the library (currently ApeKI or
	 * PstI).
	 * @param fastqDirectory Directory containing the fastq files (will be
	 * recursively searched).
	 * @param outputDir Directory to which the tagCounts files (one per fastq
	 * file) will be written.
	 * @param maxGoodReads The maximum number of barcoded reads expected in a
	 * fastq file
	 * @param minCount The minimum number of occurrences of a tag in a fastq
	 * file for it to be included in the output tagCounts file
	 */

	private class Tag {
		private final String[] taxa;
		private final int[] count;

		public Tag(String[] taxa, int[] count) {
			this.taxa = taxa;
			this.count = count;
		}
	}

	private final static Map<String, BufferedWriter> writers = 
			new HashMap<String, BufferedWriter>();
	private final static Map<Integer, Tag> indexMap = 
			new ConcurrentHashMap<Integer, Tag>();
	private static BufferedReader indexReader = null;
	private static int cursor = 0;
	private Object lock = new Object();
	private static int THREADS = 1;
	private static ExecutorService executor;
	private static int allReads = 0;
	private BlockingQueue<Runnable> tasks = null;
	private static String header = null;
	private static int is = 0;
	
	public void start() {
		tasks = new ArrayBlockingQueue<Runnable>(THREADS);
		executor = new ThreadPoolExecutor(THREADS, 
				THREADS, 
				1, 
				TimeUnit.SECONDS, 
				tasks, 
				new RejectedExecutionHandler(){
			@Override
			public void rejectedExecution(Runnable task,
					ThreadPoolExecutor arg1) {
				// TODO Auto-generated method stub
				try {
					tasks.put(task);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	public void distributeTags() {
		if(this.mySamIsSorted) 
			distributeSortedTags();
		else
			distributeUnsortedTags();
	}

	private void cache() {
		// TODO Auto-generated method stub
		try {
			indexMap.clear();
			String temp = "";
			int block = 10000;
			String[] Qs = new String[block];
			int k = 0;
			int buffer = 10000000, cached = 0;
			start();
			while( cached<buffer && temp!=null ) {
				temp = indexReader.readLine();
				if(temp!=null) {
					Qs[k] = temp;
					k++;
					cached++;
					cursor++;
				}
				if(k==block || temp==null) {
					executor.submit(new Runnable() {
						private String[] index;
						@Override
						public void run() {
							// TODO Auto-generated method stub
							int tag;
							String[] s, taxa;
							int[] count;
							for(String x : index) {
								s = x.split("\\s+");
								tag = Integer.parseInt(s[0]);
								s = s[1].split("#|:");
								taxa = new String[s.length/2];
								count = new int[s.length/2];
								for(int i=0; i<s.length/2; i++) {
									taxa[i] = s[i*2];
									count[i] = Integer.parseInt(s[i*2+1]);
								}
								indexMap.put(tag, new Tag(taxa, count));
							}
						}
						public Runnable init(String[] index) {
							this.index = index;
							return(this);
						}
					}.init(Qs));
					k=0;
					Qs = new String[block];
				}
			}
			executor.shutdown();
			executor.awaitTermination(365, TimeUnit.DAYS);
			myLogger.info("Cached indices. "+cached);
			myLogger.info("Tags processed. "+allReads);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void distributeSortedTags() {
		// TODO Auto-generated method stub
		long start = System.currentTimeMillis();
		try {
			File indexFile = new File(myIndexFileName);
			myLogger.info("Using the following index file:");
			myLogger.info(indexFile.getAbsolutePath());
			File samFile = new File(mySamFileName);
			myLogger.info("Using the following SAM file:");
			myLogger.info(samFile.getAbsolutePath());

			indexReader = Utils.getBufferedReader(indexFile, 65536);
			BufferedReader br = Utils.getBufferedReader(samFile, 65536);
			
			cache();
			start();
			
			String temp;
			int block = 10000;
			String[] Qs = new String[block];
			int k = 0;
			allReads = 0;
			StringBuilder sb = new StringBuilder();
			while( (temp= br.readLine())!=null && 
					temp.startsWith("@")) 
				sb.append(temp+"\n");
			header = sb.toString();
			int tag;
			while ( temp != null ) {
				Qs[k] = temp;
				temp = br.readLine();
				k++;
				tag = temp==null ? 0 : 
					Integer.parseInt(temp.substring(0,10).
							split("\\s+",2)[0]);
				if(k==block || temp==null || tag>=cursor) {

					executor.submit(new Runnable() {
						private String[] sam;
						@Override
						public void run() {
							// TODO Auto-generated method stub

							int tag;
							Tag tagObj;
							String taxa;
							for(int i=0; i<sam.length; i++) {
								try {
									if(sam[i]==null)
										break;
									synchronized(lock) {
										allReads++;
									}

									tag = Integer.parseInt(sam[i].substring(0,10).
											split("\\s+",2)[0]);
									if (allReads % 1000000 == 0) 
										myLogger.info("Total Reads:" + allReads);
									synchronized(lock) {
										tagObj = indexMap.get(tag);
										//if(tagObj==null) continue;
										for(int t=0; t<tagObj.taxa.length; t++) {
											taxa = tagObj.taxa[t];
											if(!writers.containsKey(taxa)) { 
												writers.put(taxa, Utils.getBufferedWriter(
														myOutputDir+
														System.getProperty("file.separator")+
														taxa+".sam"));
												writers.get(taxa).write(header);
											}

											for(int r=0; r++<tagObj.count[t];)
												writers.get(taxa).write(sam[i]+"\n");
										}
									}
								} catch (Exception e) {
									Thread t = Thread.currentThread();
									t.getUncaughtExceptionHandler().uncaughtException(t, e);
									e.printStackTrace();
									executor.shutdown();
									System.exit(1);
								}
							}
						}

						public Runnable init(String[] sam) {
							this.sam = sam;
							return(this);
						}
					}.init(Qs));
					
					k=0;
					Qs = new String[block];
					if(temp==null || tag>=cursor) {
						executor.shutdown();
						executor.awaitTermination(365, TimeUnit.DAYS);
						if(temp!=null) {
							cache();
							start();
						}
					}
				}
			}
			br.close();
			//executor.shutdown();
			//executor.awaitTermination(365, TimeUnit.DAYS);
			for(String key : writers.keySet())
				writers.get(key).close();
			myLogger.info("Total number of reads in lane=" + allReads);
			myLogger.info("Process took " + (System.currentTimeMillis() - start)/1000 + " seconds.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void distributeUnsortedTags() {

		long start = System.currentTimeMillis();
		//executor = Executors.newFixedThreadPool(THREADS);
		try {
			start();
			File indexFile = new File(myIndexFileName);
			myLogger.info("Using the following index file:");
			myLogger.info(indexFile.getAbsolutePath());
			BufferedReader br = Utils.getBufferedReader(indexFile, 65536);
			String temp;
			int block = 10000;
			String[] Qs = new String[block];
			int k = 0;
			temp = br.readLine();
			while( temp!=null ) {
				Qs[k] = temp;
				temp = br.readLine();
				k++;
				if(k==block || temp==null) {
					executor.submit(new Runnable() {
						private String[] index;
						@Override
						public void run() {
							// TODO Auto-generated method stub
							int tag;
							String[] s, taxa;
							int[] count;
							for(String x : index) {
								s = x.split("\\s+");
								tag = Integer.parseInt(s[0]);
								s = s[1].split("#|:");
								taxa = new String[s.length/2];
								count = new int[s.length/2];
								for(int i=0; i<s.length/2; i++) {
									taxa[i] = s[i*2];
									count[i] = Integer.parseInt(s[i*2+1]);
								}
								indexMap.put(tag, new Tag(taxa, count));
								synchronized(lock) {
									is++;
								}
							}
						}
						public Runnable init(String[] index) {
							this.index = index;
							return(this);
						}
					}.init(Qs));
					k=0;
					Qs = new String[block];
				}
			}
			br.close();
			executor.shutdown();
			executor.awaitTermination(365, TimeUnit.DAYS);
			myLogger.info("Loading indices done. "+is+" tags.");

			start();
			File samFile = new File(mySamFileName);
			myLogger.info("Using the following SAM file:");
			myLogger.info(samFile.getAbsolutePath());

			br = Utils.getBufferedReader(samFile, 65536);

			Qs = new String[block];
			k = 0;
			allReads = 0;
			StringBuilder sb = new StringBuilder();
			while( (temp= br.readLine())!=null && 
					temp.startsWith("@")) 
				sb.append(temp+"\n");
			header = sb.toString();

			while ( temp != null ) {
				Qs[k] = temp;
				temp = br.readLine();
				k++;
				if(k==block || temp==null) {

					executor.submit(new Runnable() {
						private String[] sam;
						@Override
						public void run() {
							// TODO Auto-generated method stub

							int tag;
							Tag tagObj;
							String taxa;
							for(int i=0; i<sam.length; i++) {
								try {
									if(sam[i]==null)
										break;
									synchronized(lock) {
										allReads++;
									}

									tag = Integer.parseInt(sam[i].substring(0,10).
											split("\\s+",2)[0]);
									if (allReads % 1000000 == 0) 
										myLogger.info("Total Reads:" + allReads);
									synchronized(lock) {
										tagObj = indexMap.get(tag);
										//if(tagObj==null) continue;
										for(int t=0; t<tagObj.taxa.length; t++) {
											taxa = tagObj.taxa[t];
											if(!writers.containsKey(taxa)) { 
												writers.put(taxa, Utils.getBufferedWriter(
														myOutputDir+
														System.getProperty("file.separator")+
														taxa+".sam"));
												writers.get(taxa).write(header);
											}

											for(int r=0; r++<tagObj.count[t];)
												writers.get(taxa).write(sam[i]+"\n");
										}
									}
								} catch (Exception e) {
									Thread t = Thread.currentThread();
									t.getUncaughtExceptionHandler().uncaughtException(t, e);
									e.printStackTrace();
									executor.shutdown();
									System.exit(1);
								}
							}
						}

						public Runnable init(String[] sam) {
							this.sam = sam;
							return(this);
						}
					}.init(Qs));
					k=0;
					Qs = new String[block];
				}
			}
			br.close();
			executor.shutdown();
			executor.awaitTermination(365, TimeUnit.DAYS);
			for(String key : writers.keySet())
				writers.get(key).close();
			myLogger.info("Total number of reads in lane=" + allReads);
			myLogger.info("Process took " + (System.currentTimeMillis() - start)/1000 + " seconds.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static double maxMemory() {
		return instance.maxMemory() / mb;
	}

	private static double totalMemory() {
		return instance.totalMemory() / mb;
	}

	private static double freeMemory() {
		return instance.freeMemory() / mb;
	}

	private static double usedMemory() {
		return totalMemory()-freeMemory();
	}

	private static void usage() {
		myLogger.info("Max Memory: "+maxMemory());
		myLogger.info("Total Memory: "+totalMemory());
		myLogger.info("Free Memory: "+freeMemory());
		myLogger.info("Used Memory: "+usedMemory());
	}
}