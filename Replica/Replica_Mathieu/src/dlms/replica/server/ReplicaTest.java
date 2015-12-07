package dlms.replica.server;

import java.net.InetSocketAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import shared.data.Bank;
import shared.data.ServerInfo;
import shared.util.Constant;
import shared.util.Env;

/**
 * 
 * 
 * @author mat
 *
 */
public class ReplicaTest {

	private BankReplica roy = null;
	private BankReplica nat = null;
	private BankReplica dom = null;

	/**
	 * Constructor
	 * @throws InterruptedException 
	 */
	public ReplicaTest() throws InterruptedException {
		
		super();

		Env.loadSettings();
		Env.setCurrentBank(Bank.Royal);
		
		startReplicas();

		testOpenAccount();
		testGetLoan();
		
		
		//testTransferLoan();
		//testDelayPayment();
	}

	public static void main(String[] args) throws InterruptedException {
		
		new ReplicaTest();

	}
	
	/**
	 * 
	 */
	public void dummyTestMethod() {
		//		OpenAccountMessage opMsg = new OpenAccountMessage(Bank.Royal.toString(),
		//		"Pascal" + 1, "Tozzi" + 1, "ptozzi@example.com" + 1, "555-555-5555", "123456" + 1);
		//
		//UDPMessage udpMsg = new UDPMessage(opMsg);
		//udpMsg.setSequenceNumber(1);
		//
		//byte[] msg = new byte[4096];
		//try {
		//	msg = Serializer.serialize(udpMsg);
		//} catch (IOException e) {
		//	e.printStackTrace();
		//}
		//
		//InetSocketAddress remoteAddr = new InetSocketAddress("localhost", 4516);
		//UdpSend sender = new UdpSend(msg, remoteAddr);
		//try {
		//	sender.call();
		//} catch (SocketException e) {
		//	e.printStackTrace();
		//} catch (IOException e) {
		//	e.printStackTrace();
		//}
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

				try {
					roy.openAccount("John", "Doe", "jondoe@gmail.com", "5145145145", "jondoe");
					nat.openAccount("Charles", "Xavier", "charlesxavier@gmail.com", "5145145145", "charlesxavier");
					dom.openAccount("Joey", "Vega", "joeyvega@gmail.com", "5145145155", "joeyvega");
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		};
		final Thread tc2 = new Thread() {
			@Override
			public void run() {

				try {
					dom.openAccount("Joey", "Vega", "joeyvega@gmail.com", "5145145155", "joeyvega");
					nat.openAccount("John", "Doe", "jondoe@gmail.com", "5145145145", "jondoe");
					roy.openAccount("Jane", "Conor", "janeconor@gmail.com", "5145145144", "janeconor");
					nat.openAccount("Josh", "Lane", "Joshlane@gmail.com", "5145145244", "joshlane");
					dom.openAccount("Jules", "Winnfield", "juleswinnfield@gmail.com", "5145145144", "juleswinnfield");
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		};
		final Thread tc3 = new Thread() {
			@Override
			public void run() {

				try {
					dom.openAccount("John", "Doe", "jondoe@gmail.com", "5145145145", "jondoe");
					roy.openAccount("Kyle", "Reese", "kylereese@gmail.com", "5145145163", "kylereese");
					nat.openAccount("Elanor", "Gamgee", "elanorgamgee@gmail.com", "5145145343", "elanorgamgee");
					dom.openAccount("John", "Doe", "jondoe@gmail.com", "5145145145", "jondoe");
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
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
				
				System.out.println(roy.printCustomerInfo());
				System.out.println(nat.printCustomerInfo());
				System.out.println(dom.printCustomerInfo());
			}
		};
		
		tm1.start();
		tm1.join();
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
				int accNbr;
				try {
					accNbr = roy.openAccount("John", "Doe", "jondoe@gmail.com", "5145145145", "jondoe");
					roy.getLoan(accNbr, "jondoe", 500);
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		};
	
		final Thread tc2 = new Thread() {
			@Override
			public void run() {
				int accNbr;
				try {
					accNbr = dom.openAccount("John", "Doe", "jondoe@gmail.com", "5145145145", "jondoe");
					dom.getLoan(accNbr, "jondoe", 500);
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		};
	
		final Thread tc3 = new Thread() {
			@Override
			public void run() {
				int accNbr;
				try {
					accNbr = nat.openAccount("John", "Doe", "jondoe@gmail.com", "5145145145", "jondoe");
					nat.getLoan(accNbr, "jondoe", 600);
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
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
				
				System.out.println(roy.printCustomerInfo());
				System.out.println(nat.printCustomerInfo());
				System.out.println(dom.printCustomerInfo());
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
		
		int accNbr = -1;
		try {
			accNbr = roy.openAccount("John", "Doe", "jondoe@gmail.com", "5145145145", "jondoe");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return;
		}
		
		// Add a loan for user jondoe@gmail.com at bank "rbc"
		int loanId = -1;
		try {
			loanId = roy.getLoan(accNbr, "jondoe", 500);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return;
		}
		if (loanId < 1) {
			System.out.println("Loan creation failed. {bank: rbc, accountNbr: " + accNbr + ", name: jondoe, amount: 500}");
			return;
		}

		// Before loan transfer
		System.out.println(roy.printCustomerInfo());
		System.out.println(dom.printCustomerInfo());
		
		// Transfer a loan
		try {
			roy.transferLoan(loanId, "Dominion");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return;
		}
		
		// After loan transfer
		System.out.println(roy.printCustomerInfo());
		System.out.println(dom.printCustomerInfo());
	}
	
	/**
	 * Test method #3 - Tests concurrency for the DelayPayment operation
	 * 
	 * @throws InterruptedException
	 */
	public Boolean testDelayPayment() {

		// Create an account
		int accNbr;
		try {
			accNbr = roy.openAccount("John", "Doe", "jondoe@gmail.com", "5145145145", "jondoe");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
		
		// Add a loan for user jondoe@gmail.com at bank "rbc"
		int loanId;
		try {
			loanId = roy.getLoan(accNbr, "jondoe", 500);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
		
		System.out.println(roy.printCustomerInfo());
		
		if (loanId > 0) {

			Date startDate = null;
			Date endDate = null;
			try {
				startDate = new SimpleDateFormat("dd-MM-yyyy").parse("31-12-2015");
				endDate = new SimpleDateFormat("dd-MM-yyyy").parse("31-06-2016");
			} catch (ParseException e) {
				System.out.println(e.getMessage());
				return false;
			}
			
			try {
				roy.delayPayment(loanId, startDate, endDate);
			} catch (Exception e) {
				System.out.println(e.getMessage());
				return false;
			}
			
			System.out.println(roy.printCustomerInfo());
			return true;
		}
		
		return false;
	}
	
	/**
	 * Keep the config (Env class) outside the implementation
	 * 
	 * @return
	 */
	public BankReplicaStubGroup getReplicaStubs() {

		ServerInfo bank1Info = Env.getReplicaIntranetServerInfo(Constant.MACHINE_NAME_MATHIEU, Bank.Royal);
		ServerInfo bank2Info = Env.getReplicaIntranetServerInfo(Constant.MACHINE_NAME_MATHIEU, Bank.National);
		ServerInfo bank3Info = Env.getReplicaIntranetServerInfo(Constant.MACHINE_NAME_MATHIEU, Bank.Dominion);
		
		BankReplicaStubGroup replicaStubs = new BankReplicaStubGroup();
		replicaStubs.put(Bank.Royal.name(), new BankReplicaStub(Bank.Royal.name(), new InetSocketAddress(bank1Info.getIpAddress(), bank1Info.getPort())));
		replicaStubs.put(Bank.National.name(), new BankReplicaStub(Bank.National.name(), new InetSocketAddress(bank2Info.getIpAddress(), bank2Info.getPort())));
		replicaStubs.put(Bank.Dominion.name(), new BankReplicaStub(Bank.Dominion.name(), new InetSocketAddress(bank3Info.getIpAddress(), bank3Info.getPort())));
		
		return replicaStubs;
	}
	
	/**
	 * 
	 */
	public void startReplicas() {
		
		roy = new BankReplica(Bank.Royal.name(), getReplicaStubs(), 9999);
		nat = new BankReplica(Bank.National.name(), getReplicaStubs(), 9998);
		dom = new BankReplica(Bank.Dominion.name(), getReplicaStubs(), 9997);
	}

}
