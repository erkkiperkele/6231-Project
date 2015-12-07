package dlms.client;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;

/**
 * Sample test client launcher
 * 
 * @author mat
 *
 */
public class TestClient {

	/**
	 * 
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String args[]) throws InterruptedException {

		CustomerClient cc = new CustomerClient();
		
		int accontNbr1 = cc.openAccount("rbc", "John", "Doe", "jondoe@gmail.com", "5145145145", "jondoe");
		int accontNbr2 = cc.openAccount("bmo", "Charles", "Xavier", "charlesxavier@gmail.com", "5145145145", "charlesxavier");
		int accontNbr3 = cc.openAccount("cibc", "Joey", "Vega", "joeyvega@gmail.com", "5145145155", "joeyvega");

		System.out.println("Created account " + accontNbr1);
		System.out.println("Created account " + accontNbr2);
		System.out.println("Created account " + accontNbr3);
		
		// Create a manager client and output user data
		ManagerClient mc = new ManagerClient();
		
//		mc.printCustomerInfo("rbc");
//		mc.printCustomerInfo("bmo");
//		mc.printCustomerInfo("cibc");

//		test();
//		
		// Create a few customer clients in their own threads and make them do some operations
		final Thread tc1 = new Thread() {
			@Override
			public void run() {
				mc.printCustomerInfo("rbc");
				//long threadId = Thread.currentThread().getId();
				//System.out.println("Thread tc1 # " + threadId + " is doing this task");
			}
		};
	
		final Thread tc2 = new Thread() {
			@Override
			public void run() {
				mc.printCustomerInfo("bmo");
				//long threadId = Thread.currentThread().getId();
				//System.out.println("Thread tc2 # " + threadId + " is doing this task");
			}
		};
	
		final Thread tc3 = new Thread() {
			@Override
			public void run() {
				mc.printCustomerInfo("cibc");
				//long threadId = Thread.currentThread().getId();
				//System.out.println("Thread tc3 # " + threadId + " is doing this task");
			}
		};

		tc1.start();
		tc2.start();
		tc3.start();
		tc1.join();
		tc2.join();
		tc3.join();
	}
	
	public static void test() {
		RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
		 
		String jvmName = runtimeBean.getName();
		System.out.println("JVM Name = " + jvmName);
		long pid = Long.valueOf(jvmName.split("@")[0]);
		System.out.println("JVM PID  = " + pid);
 
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
 
		int peakThreadCount = bean.getPeakThreadCount();
		System.out.println("Peak Thread Count = " + peakThreadCount);
	}

}
