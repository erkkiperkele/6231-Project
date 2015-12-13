package impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.SocketException;

import shared.data.*;
import shared.util.Env;
import shared.udp.UdpReplicaServiceThread;
import shared.exception.*;

public class BankServer extends AbstractServerBank {
	
	private static final int NUMOFBANKS = Bank.getBanks().length;

	private BankStore bankStore;
	private int port;
	private String name;
	private UdpReplicaServiceThread udpServer = null;
	private UDPIntranetListenerThread listener = null;
	private static int LoanNumber = 0;
	private static int CustomerNumber = 0;
	
	
	private static synchronized int incrementCustomerNumber() {
		return ++CustomerNumber;
	}
	
	private static synchronized int incrementLoanNumber() {
		return ++LoanNumber;
	}

	public BankServer(BankStore bankStore, ServerInfo svInfo) {
		this.bankStore = bankStore;
		this.port = svInfo.getPort();
		this.name = Env.getCurrentBank().toString();
		
		// Start UDPServerThread
		try {
			udpServer = new UdpReplicaServiceThread("Richard replica implementation", port, this);
			listener = new UDPIntranetListenerThread(bankStore);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		listener.start();
		udpServer.start();
	}

	@Override
	public int openAccount(String FirstName, String LastName, String EmailAddress, String PhoneNumber,
			String Password) {
		Customer c = new Customer(FirstName, LastName, EmailAddress, PhoneNumber, Password);
		bankStore.storeCustomer(c);
		return c.getId();
	}

	@Override
	public int getLoan(int AccountNumber, String Password, long Amount) throws Exception {
		// Verify that AccountNumber exists in the system
		Customer c = bankStore.getCustomer(AccountNumber);
		if (c == null) {
			// No account with that AccountNumber exists on this server
			throw new ExceptionNotValidCustomerAccountID();
		}

		// Verify that customer does not already have a loan
		if (bankStore.getLoanByCustomer(c.getAccountNumber()) != null) {
			throw new ExceptionOnlyOneLoanPerCustomer();
		}
		
		// Password check
		if (!c.getPassword().equals(Password)) {
			throw new ExceptionInvalidPassword();
		}

		String fn = c.getFirstName();
		String ln = c.getLastName();
		
		ArrayList<Long> bankResponses = new ArrayList<Long>(NUMOFBANKS);
		CountDownLatch latch = new CountDownLatch(NUMOFBANKS-1);
		for (int i = 0; i < NUMOFBANKS; ++i) {
			
			int udpport = Env.getReplicaIntranetServerInfo(Bank.getBanks()[i]).getPort();
			// Check own bank
			if (udpport == this.port) {
				synchronized(bankResponses) {
						bankResponses.add((long)0);
				}
				continue;
			}
			
			// Spawn new thread to poll BankServer
			// Response from thread is automatically added to responses.
			new UDPLoanRequesterThread(udpport, fn + " " + ln, bankResponses, latch).run();
		}
		// Wait for all threads to finish
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// Parse responses from all banks
		double customerLoans = 0.0;
		for (int i = 0; i < NUMOFBANKS; ++i) {
			customerLoans += bankResponses.get(i);
		}

		if ((Amount + customerLoans) > c.getCreditLimit()) {
			// Loan amount too high
			bankStore.refuseLoan(c, Amount);
			throw new Exception("Loan refused: Not enough credit.\n");
		}

		Loan l = new Loan(incrementLoanNumber(), c.getId(), Amount, Env.getNewLoanDueDate());
		bankStore.storeLoan(c, l);

		return l.getLoanNumber();
	}

	@Override
	public boolean delayPayment(int LoanID, Date CurrentDueDate, Date NewDueDate) throws Exception {
		Loan l = bankStore.getLoan(LoanID);

		// Check that loan exists
		if (l == null) {
			throw new Exception("Failed. No such loan.\n");
		}

		// Check that current due date is correct
		if (!l.getDueDate().equals(CurrentDueDate)) {
			throw new Exception("Failed. Due date mismatch\n");
		}

		bankStore.changeLoanRepayment(l, NewDueDate);
		return true;
	}

	@Override
	public String printCustomerInfo() {
		return bankStore.printInfo();
	}

	@Override
	public boolean transferLoan(int LoanID, String OtherBank) throws Exception {
		// Check if loan exists
		Loan loan = bankStore.getLoan(LoanID);
		if (loan == null) {
			throw new ExceptionInvalidLoanID();
		}
		InetAddress lh = InetAddress.getLocalHost();
		String r;
		// Create separate socket for UDP communication
		BankSocket sock = new BankSocket();

		// Get OtherBank's UDP port
		int rp = Env.getReplicaIntranetServerInfo(Bank.fromString(OtherBank)).getPort();
		// Send OtherBank a ping to create a new socket
		sock.sendMessage(lh, rp, "Loan Transfer Request");
		// Collect remote socket info
		r = sock.recvMessage();
		if (r.equals("ACK")) {
			rp = sock.getResponsePort();
		}
		Customer custAcc = bankStore.getCustomer(loan.getCustomerAccountNumber());
		ObjectOutputStream send = new ObjectOutputStream(sock.getOutputStream());
		send.writeObject(custAcc);
		send.writeObject(loan);
		ObjectInputStream recv = new ObjectInputStream(sock.getInputStream());
		boolean result = recv.readBoolean();
		if (result) {
			// Loan transferred successfully. Delete local copy
			bankStore.removeLoan(LoanID);
		}
		sock.close();
		return result;
	}

	/**
	 * Listens for communication from other BankServers. Launches responders
	 * @author Richard
	 *
	 */
	private class UDPIntranetListenerThread extends Thread {
		public static final int BUFFER_SIZE = 4096;
		
		private BankSocket socket;
		private BankStore bank;
		byte[] buffer = new byte[BUFFER_SIZE];

		public UDPIntranetListenerThread(BankStore bank) {
			this.bank = bank;
			try {
				this.socket = new BankSocket(Env.getReplicaIntranetServerInfo(bank.getName()).getPort());
			} catch (SocketException e) {
				e.printStackTrace();
			}
		}

		public void run() {
			while(true) {
				try {
					// Get message and sender information
					String msg = socket.recvMessage();
					// Handle request
					if (msg.equals("Loan Transfer Request")) {
						new UDPLoanReceiverThread(bank, socket.getInetAddress(), socket.getPort());
					} else {
						// Incoming message was name of Customer
						String[] name = msg.split(" ");
						Customer c = bank.getCustomer(name[0], name[1]);
						if (c == null) {
							socket.sendReply("0");
						} else {
							Loan l = bank.getLoan(c.getAccountNumber());
							if (l == null) {
								socket.sendReply("0");
							} else {
								socket.sendReply(String.valueOf(l.getAmount()));
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
					continue;
				}
			}
		}
	}

	private class UDPLoanRequesterThread extends Thread {
		private int remoteport;
		private String msg;
		private String response;
		private ArrayList<Long> responses;
		private CountDownLatch latch;
		public UDPLoanRequesterThread(int remoteport, String msg, ArrayList<Long> responses, CountDownLatch latch) {
			this.remoteport = remoteport;
			this.msg = msg;
			this.responses = responses;
			this.latch = latch;
		}

		public void run() {
			try {
				// Create socket on any available port, send message
				BankSocket b = new BankSocket();
				b.sendMessage(InetAddress.getLocalHost(), remoteport, msg);
				
				// Collect response into collection
				response = b.recvMessage();
				b.close();
				synchronized(responses) {
					responses.add(Long.parseLong(response));
				}
				// When thread finishes evaluating, call countDown()
				latch.countDown();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private class UDPLoanReceiverThread extends Thread {
		private BankSocket sock;
		private InetAddress remoteHost;
		private int remotePort;
		private BankStore b;
		
		public UDPLoanReceiverThread(BankStore b, InetAddress rh, int rp) {
			this.b = b;
			try {
				// Create new bank socket
				this.sock = new BankSocket();
				this.remoteHost = rh;
				this.remotePort = rp;
			} catch (SocketException e) {
				e.printStackTrace();
			}
		}
		
		public void run() {
			// Send ACK
			send("ACK");
			try {
				ObjectInputStream recv = new ObjectInputStream(sock.getInputStream());
				// Wait to receive Customer and Loan
				Customer custRecv = (Customer)recv.readObject();
				Loan loanRecv = (Loan)recv.readObject();
				// Check if customer exists
				Customer custAcc = b.getCustomer(custRecv.getFirstName(), custRecv.getLastName());
				if (custAcc == null) {
					// Store new Customer
					custAcc = new Customer(custRecv.getFirstName(), custRecv.getLastName(),
							custRecv.getPassword(), custRecv.getEmail(), custRecv.getPhone());
					custAcc.setAccountNumber(incrementCustomerNumber());
					b.storeCustomer(custAcc);
				}
				custAcc.setAccountNumber(incrementCustomerNumber());

				// Store new Loan
				Loan loanToStore = new Loan(incrementLoanNumber(), custAcc.getAccountNumber(), loanRecv.getAmount(), loanRecv.getDueDate());
				b.storeLoan(custAcc, loanToStore);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void send(String s) {
			try {
				sock.sendMessage(remoteHost, remotePort, s);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String getServerName() {
		return this.name;
	}

	@Override
	public BankState getCurrentState() {
		return new BankState(bankStore.getLoanState(), bankStore.getCustomerState(), CustomerNumber + 1, LoanNumber + 1);
	}

	@Override
	public void setCurrentState(BankState state) {
		bankStore.setCurrentState(state);
	}
	
	public void waitUntilFinished() throws InterruptedException {
		udpServer.join();
	}
}

