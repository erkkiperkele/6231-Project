package dlms.replica;

import dlms.replica.client.CustomerClient;
import dlms.replica.client.ManagerClient;

/**
 * The test client. It creates a few clients to test the web services
 * 
 * @author mat
 *
 */
public class TestApp {

	/**
	 * Main client method 
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new TestApp();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Constructor
	 * 
	 * @throws InterruptedException 
	 */
	public TestApp() throws InterruptedException {
		
		super();

		Thread serverthread = new Thread(new ServerApp());
		serverthread.start();

		
		/* ONLY ONE OF THE FOLLOWING TEST METHODS SHOULD BE RUN PER EXECUTION 
		 * MAKE SURE TO COMMENT ALL EXCEPT ONE */
		
		
		/* ********* TEST OPEN ACCOUNT *****************/
		// This method runs a bunch of openAccount operations at the same time
		// and makes sure no bank contains two of the same account
		this.testOpenAccount();
		
		/* ********* TEST GET LOAN *****************/
		// This method's results, run multiple times, should alternate between a
		// scenario where bank 3 gets a loan of 600 and, a scenario where where
		// banks 1 and 2 both get a loan for 500
		//this.testGetLoan();

		/* ********* TEST DELAY PAYMENT *****************/
		// This method tests the delayPament operation
		//this.testDelayPayment();
		
		/* ********* TEST TRANSFER LOAN *****************/
		// Transfers a loan from one bank to the other
		//this.testTransferLoan();

		
		serverthread.join();
	}
	
	/**
	 * Test method #3 - Tests concurrency for the DelayPayment operation
	 * 
	 * @throws InterruptedException
	 */
	public Boolean testDelayPayment() {
		
		CustomerClient cc = new CustomerClient();
		
		// Create an account
		int accNbr = cc.openAccount("rbc", "John", "Doe", "jondoe@gmail.com", "5145145145", "jondoe");
		
		// Add a loan for user jondoe@gmail.com at bank "rbc"
		int loanId = cc.getLoan("rbc", accNbr, "jondoe", 500);
		
		if (loanId > 0) {
			ManagerClient mc = new ManagerClient();
			mc.delayPayment("rbc", loanId, "31-12-2015", "31-06-2016");	
			mc.printCustomerInfo("rbc");
			return true;
		}
		
		return false;
	}

	/**
	 * Test method #4 - Tests concurrency for the GetLoan operation
	 * 
	 * @throws InterruptedException
	 */
	public void testGetLoan() throws InterruptedException {
	
		// Create a few customer clients in their own threads and make them do some operations
		final Thread tc1 = new Thread() {
			@Override
			public void run() {
				CustomerClient cc = new CustomerClient();
				int accNbr = cc.openAccount("rbc", "John", "Doe", "jondoe@gmail.com", "5145145145", "jondoe");
				// Add a loan for user jondoe@gmail.com at bank "rbc"
				cc.getLoan("rbc", accNbr, "jondoe", 500);
			}
		};
	
		final Thread tc2 = new Thread() {
			@Override
			public void run() {
				CustomerClient cc = new CustomerClient();
				int accNbr = cc.openAccount("bmo", "John", "Doe", "jondoe@gmail.com", "5145145145", "jondoe");
				// Add a loan for user jondoe@gmail.com at bank "bmo"
				cc.getLoan("bmo", accNbr, "jondoe", 500);
			}
		};
	
		final Thread tc3 = new Thread() {
			@Override
			public void run() {
				CustomerClient cc = new CustomerClient();
				int accNbr = cc.openAccount("cibc", "John", "Doe", "jondoe@gmail.com", "5145145145", "jondoe");
				// Add a loan for user jondoe@gmail.com at bank "cibc"
				cc.getLoan("cibc", accNbr, "jondoe", 600);
			}
		};
	
		tc1.start();
		tc2.start();
		tc3.start();
		tc1.join();
		tc2.join();
		tc3.join();
		
		final Thread tm1 = new Thread() {
			@Override
			public void run() {
				//System.out.println("Starting manager client #1");
				ManagerClient mc = new ManagerClient();
				mc.printCustomerInfo("rbc");
				mc.printCustomerInfo("bmo");
				mc.printCustomerInfo("cibc");
			}
		};
	
		tm1.start();
		tm1.join();
	}
	
	/** 
	 * Test method #1 - Tests concurrency for OpenAccount operation
	 * 
	 * @throws InterruptedException
	 */
	public void testOpenAccount() throws InterruptedException {

		final Thread tc1 = new Thread() {
			@Override
			public void run() {
				CustomerClient cc = new CustomerClient();
				cc.openAccount("rbc", "John", "Doe", "jondoe@gmail.com", "5145145145", "jondoe");
				cc.openAccount("bmo", "Charles", "Xavier", "charlesxavier@gmail.com", "5145145145", "charlesxavier");
				cc.openAccount("cibc", "Joey", "Vega", "joeyvega@gmail.com", "5145145155", "joeyvega");
			}
		};
		final Thread tc2 = new Thread() {
			@Override
			public void run() {
				CustomerClient cc = new CustomerClient();
				cc.openAccount("cibc", "Joey", "Vega", "joeyvega@gmail.com", "5145145155", "joeyvega");
				cc.openAccount("bmo", "John", "Doe", "jondoe@gmail.com", "5145145145", "jondoe");
				cc.openAccount("rbc", "Jane", "Conor", "janeconor@gmail.com", "5145145144", "janeconor");
				cc.openAccount("bmo", "Josh", "Lane", "Joshlane@gmail.com", "5145145244", "joshlane");
				cc.openAccount("cibc", "Jules", "Winnfield", "juleswinnfield@gmail.com", "5145145144", "juleswinnfield");
			}
		};
		final Thread tc3 = new Thread() {
			@Override
			public void run() {
				CustomerClient cc = new CustomerClient();
				cc.openAccount("cibc", "Joey", "Vega", "joeyvega@gmail.com", "5145145155", "joeyvega");
				cc.openAccount("cibc", "John", "Doe", "jondoe@gmail.com", "5145145145", "jondoe");
				cc.openAccount("rbc", "Kyle", "Reese", "kylereese@gmail.com", "5145145163", "kylereese");
				cc.openAccount("bmo", "Elanor", "Gamgee", "elanorgamgee@gmail.com", "5145145343", "elanorgamgee");
				cc.openAccount("cibc", "John", "Doe", "jondoe@gmail.com", "5145145145", "jondoe");
			}
		};

		tc1.start();
		tc2.start();
		tc3.start();
		tc1.join();
		tc2.join();
		tc3.join();

		final Thread tm1 = new Thread() {
			@Override
			public void run() {
				ManagerClient mc = new ManagerClient();
				mc.printCustomerInfo("rbc");
				mc.printCustomerInfo("cibc");
				mc.printCustomerInfo("bmo");
			}
		};
		
		tm1.start();
		tm1.join();
	}
	
	/**
	 * Test method #4 - Tests concurrency for the DelayLoan operation
	 * 
	 * @throws InterruptedException
	 */
	public void testTransferLoan() throws InterruptedException {

		//System.out.println("Starting manager client #1");
		ManagerClient mc = new ManagerClient();
		CustomerClient cc = new CustomerClient();
		
		int accNbr = cc.openAccount("rbc", "John", "Doe", "jondoe@gmail.com", "5145145145", "jondoe");
		
		// Add a loan for user jondoe@gmail.com at bank "rbc"
		int loanId = cc.getLoan("rbc", accNbr, "jondoe", 500);
		if (loanId < 1) {
			System.out.println("Loan creation failed. {bank: rbc, accountNbr: " + accNbr + ", name: jondoe, amount: 500}");
			return;
		}

		// Before loan transfer
		mc.printCustomerInfo("rbc");
		mc.printCustomerInfo("bmo");
		
		// Transfer a loan
		cc.transferLoan(loanId, "rbc", "bmo");
		
		// After loan transfer
		mc.printCustomerInfo("rbc");
		mc.printCustomerInfo("bmo");
	}
}
