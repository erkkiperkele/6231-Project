package dlms.replica.server;

import java.net.InetSocketAddress;

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
	}

	public static void main(String[] args) throws InterruptedException {
		
		new ReplicaTest();

//		OpenAccountMessage opMsg = new OpenAccountMessage(Bank.Royal.toString(),
//				"Pascal" + 1, "Tozzi" + 1, "ptozzi@example.com" + 1, "555-555-5555", "123456" + 1);
//
//		UDPMessage udpMsg = new UDPMessage(opMsg);
//		udpMsg.setSequenceNumber(1);
//		
//		byte[] msg = new byte[4096];
//		try {
//			msg = Serializer.serialize(udpMsg);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		InetSocketAddress remoteAddr = new InetSocketAddress("localhost", 4516);
//		UdpSend sender = new UdpSend(msg, remoteAddr);
//		try {
//			sender.call();
//		} catch (SocketException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
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

				roy.openAccount("John", "Doe", "jondoe@gmail.com", "5145145145", "jondoe");
				nat.openAccount("Charles", "Xavier", "charlesxavier@gmail.com", "5145145145", "charlesxavier");
				dom.openAccount("Joey", "Vega", "joeyvega@gmail.com", "5145145155", "joeyvega");
			}
		};
		final Thread tc2 = new Thread() {
			@Override
			public void run() {

				dom.openAccount("Joey", "Vega", "joeyvega@gmail.com", "5145145155", "joeyvega");
				nat.openAccount("John", "Doe", "jondoe@gmail.com", "5145145145", "jondoe");
				roy.openAccount("Jane", "Conor", "janeconor@gmail.com", "5145145144", "janeconor");
				nat.openAccount("Josh", "Lane", "Joshlane@gmail.com", "5145145244", "joshlane");
				dom.openAccount("Jules", "Winnfield", "juleswinnfield@gmail.com", "5145145144", "juleswinnfield");
			}
		};
		final Thread tc3 = new Thread() {
			@Override
			public void run() {

				dom.openAccount("Joey", "Vega", "joeyvega@gmail.com", "5145145155", "joeyvega");
				dom.openAccount("John", "Doe", "jondoe@gmail.com", "5145145145", "jondoe");
				roy.openAccount("Kyle", "Reese", "kylereese@gmail.com", "5145145163", "kylereese");
				nat.openAccount("Elanor", "Gamgee", "elanorgamgee@gmail.com", "5145145343", "elanorgamgee");
				dom.openAccount("John", "Doe", "jondoe@gmail.com", "5145145145", "jondoe");
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
