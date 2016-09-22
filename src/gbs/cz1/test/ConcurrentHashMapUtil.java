package gbs.cz1.test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ConcurrentHashMapUtil {

	private final static Map<String, Integer> testMap = 
			new HashMap<String, Integer>();
	//static {
	//	testMap.put("s", 0);
	//}
	
	private static ExecutorService executor = 
			Executors.newFixedThreadPool(3);
	private Object lock = new Object();
	private static int s = 0;
	
	public void run() 
			throws InterruptedException {
		for(int i=0; i<1000000; i++) {
			executor.submit(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					synchronized(lock) {
						if(testMap.containsKey("s"))
							testMap.put("s",
									testMap.get("s")+1);
						else
							testMap.put("s", 1);
					}
				}
			});
		}
		executor.shutdown();
		executor.awaitTermination(1, TimeUnit.DAYS);
		System.out.println(testMap.get("s"));
		
		executor = Executors.newFixedThreadPool(3);
		for(int i=0; i<1000000; i++) {
			executor.submit(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					synchronized(lock) {
						s++;
					}
				}
			});
		}
		executor.shutdown();
		executor.awaitTermination(1, TimeUnit.DAYS);
		
		System.out.println(s);
	}
	
	public static void main(String[] args) 
			throws InterruptedException {
		ConcurrentHashMapUtil chm = 
				new ConcurrentHashMapUtil();
		chm.run();
	}
}
