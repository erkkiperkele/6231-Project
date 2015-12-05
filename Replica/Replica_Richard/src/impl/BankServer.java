package impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;

import shared.data.*;
import shared.util.Env;
import shared.udp.UDPServerThread;
import shared.exception.*;

public class BankServer extends AbstractServerBank {
	
	private static int PORTSTART = 3000;
	private static final int NUMOFBANKS = 3;
	// Maintain a registry of each instance's name/port for UDP communication
	// This solution is only valid because all BankServers run on the same machine
	private static HashMap<Bank, Integer> servers = new HashMap<Bank, Integer>();

	private BankStore bank;
	private int port;
	private BankSocket socket;
	private static int LoanNumber = 0;
	private static int CustomerNumber = 0;
	
	private static synchronized int incrementCustomerNumber() {
		return ++CustomerNumber;
	}
	
	private static synchronized int incrementLoanNumber() {
		return ++LoanNumber;
	}

	public BankServer(BankStore bank) {
		this.bank = bank;
		this.port = PORTSTART++;
		
		// Store bank's UDP info in static member servers
		servers.put(this.bank.getName(), this.port);
		
		try {
			this.socket = new BankSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		// Start UDP listener thread
		new UDPListenerThread(this.bank, this.socket).start();
	}

	@Override
	public int openAccount(String FirstName, String LastName, String EmailAddress, String PhoneNumber,
			String Password) {
		Customer c = new Customer(FirstName, LastName, EmailAddress, PhoneNumber, Password);
		bank.storeAccount(c);
		return c.getId();
	}

	@Override
	public int getLoan(int AccountNumber, String Password, long Amount) throws Exception {
		// Verify that AccountNumber exists in the system
		Customer c = bank.getCustomer(AccountNumber);
		if (c == null) {
			// No account with that AccountNumber exists on this server
			throw new ExceptionNotValidCustomerAccountID();
		}

		// Password check
		if (!c.getPassword().equals(Password)) {
			throw new ExceptionInvalidPassword();
		}

		String fn = c.getFirstName();
		String ln = c.getLastName();
		
		ArrayList<String> bankResponses = new ArrayList<String>(NUMOFBANKS);
		CountDownLatch latch = new CountDownLatch(NUMOFBANKS-1);
		for (int i = 0; i < NUMOFBANKS; ++i) {
			
			int udpport = PORTSTART - i - 1;
			// Check own bank
			if (udpport == this.port) {
				Loan l = bank.getLoan(AccountNumber);
				synchronized(bankResponses) {
					if (l == null) {
						bankResponses.add("0.0");
					} else {
						bankResponses.add(String.valueOf(l.getAmount()));
					}
				}
				continue;
			}
			
			// Spawn new thread to poll BankServer
			// Response from thread is automatically added to responses.
			new UDPRequesterThread(udpport, fn + " " + ln, bankResponses, latch).run();
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
			customerLoans += Double.parseDouble(bankResponses.get(i));
		}

		if ((Amount + customerLoans) > c.getCreditLimit()) {
			// Loan amount too high
			bank.refuseLoan(c, Amount);
			throw new Exception("Loan refused: Not enough credit.\n");
		}

		Loan l = new Loan(incrementLoanNumber(), c.getId(), Amount, Env.getNewLoanDueDate());
		bank.storeLoan(c, l);

		return l.getLoanNumber();
	}

	@Override
	public boolean delayPayment(int LoanID, Date CurrentDueDate, Date NewDueDate) throws Exception {
		Loan l = bank.getLoan(LoanID);

		// Check that loan exists
		if (l == null) {
			throw new Exception("Failed. No such loan.\n");
		}

		// Check that current due date is correct
		if (!l.getDueDate().equals(CurrentDueDate)) {
			throw new Exception("Failed. Due date mismatch\n");
		}

		bank.changeLoanRepayment(l, NewDueDate);
		return true;
	}

	@Override
	public String printCustomerInfo() {
		return bank.printInfo();
	}

	@Override
	public boolean transferLoan(int LoanID, String OtherBank) throws Exception {
		// Check if loan exists
		Loan loan = bank.getLoan(LoanID);
		if (loan == null) {
			throw new Exception("No loan found with ID=" + LoanID + "\n");
		}
		try {
			InetAddress lh = InetAddress.getLocalHost();
			String r;
			// Create separate socket for UDP communication
			BankSocket sock = new BankSocket();

			// Get OtherBank's UDP port
			int rp = servers.get(OtherBank);
			// Send OtherBank a ping to create a new socket
			sock.sendMessage(lh, rp, "Loan Transfer Request");
			// Collect remote socket info
			r = sock.recvMessage();
			if (r.equals("ACK")) {
				rp = sock.getResponsePort();
			}
			
			Customer custAcc = bank.getCustomer(loan.getCustomerAccountNumber());
			String name = custAcc.getFirstName()+ " " + custAcc.getLastName();
			// Poll remote bank for Customer name
			sock.sendMessage(lh, rp, name);
			// Receive reply
			r = sock.recvMessage();
			if (r.equals("NAK")) {
				// Send CustomerAccount information
				StringBuffer caString = new StringBuffer();
				caString.append(custAcc.getEmail() + "\n");
				caString.append(custAcc.getPhoneNumber() + "\n");
				caString.append(custAcc.getPassword() + "\n");
				
				sock.sendMessage(lh, rp, caString.toString());
				// Receive reply
				r = sock.recvMessage();
				if (!(r.equals("ACK"))) {
					sock.close();
					return "Unrecognized response: " + r + "\n";
				}
			} else if (!(r.equals("ACK"))) {
				sock.close();
				return "Unrecognized response: " + r + "\n";
			}
			// Send Loan information
			sock.sendMessage(lh, rp, String.valueOf(loan.getAmount()) + "\n" + loan.getDueDate());
			// Receive reply/finalize request
			r = sock.recvMessage();
			if (!(r.equals("ACK"))) {
				sock.close();
				return "Unrecognized response: " + r + "\n";
			}
			// Send finalize message
			sock.sendMessage(lh, rp, "ACK");
			// Receive reply/finalzed confirmation
			r = sock.recvMessage();
			if (!(r.equals("ACK"))) {
				sock.close();
				return "Unrecognized response: " + r + "\n";
			}

			// Delete local loan
			bank.removeLoan(loan.getID());
			sock.close();
			return "Loan successfully transferred.\n";
		} catch (SocketException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private class UDPListenerThread extends Thread {
		BankStore bank;
		BankSocket socket;

		public UDPListenerThread(BankStore bank, BankSocket socket) {
			this.bank = bank;
			this.socket = socket;
		}

		public void run() {
			while(true) {
				try {
					// Get message and sender information
					String inc = socket.recvMessage();
					InetAddress rh = socket.getResponseIP();
					int rp = socket.getResponsePort();
					// Parse incoming message
					if (inc.equals("Loan Transfer Request")) {
						new UDPLoanReceiverThread(bank, rh, rp).start();
					} else {
						// first + last? Asking about Loan information
						// Create thread to handle request
						new UDPResponderThread(bank, inc, rh, rp).start();
					}
				} catch (IOException e) {
					e.printStackTrace();
					continue;
				}
			}
		}
	}

	private class UDPResponderThread extends Thread {
		private BankStore bank;
		private BankSocket socket;
		private InetAddress remoteHost;
		private int remotePort;
		private String command;

		public UDPResponderThread(BankStore bank, String command, InetAddress rh, int rp) {
			this.bank = bank;
			try {
				this.socket = new BankSocket();
				this.remoteHost = rh;
				this.remotePort = rp;
			} catch (SocketException e) {
				e.printStackTrace();
			}
			this.command = command;
		}

		public void run() {
			String[] sp = command.split(" ");
			// Search accounts for matching person (same name)
			String response = null;
			Customer c = bank.getCustomer(sp[0], sp[1].trim());
			if (c == null) {
				// Reply 0.0
				response = "0.0";
			} else {
				// Search for loan from person
				Loan l = bank.getLoanByCustomer(c.getId());
				if (l == null) {
					// Reply 0.0
					response = "0.0";
				} else {
					response = String.valueOf(l.getAmount());
				}
			}
			// Reply loan amount
			try {
				socket.sendMessage(remoteHost, remotePort, response);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private class UDPRequesterThread extends Thread {
		private int remoteport;
		private String msg;
		private String response;
		private ArrayList<String> responses;
		private CountDownLatch latch;
		public UDPRequesterThread(int remoteport, String msg, ArrayList<String> responses, CountDownLatch latch) {
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
					responses.add(response);
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
			// Wait to receive name of Customer
			String[] custName = recv().split(" ");
			// Check if customer exists
			Customer custAcc = b.getCustomer(custName[0], custName[1]);
			int custID;
			Customer newAcc = null;
			// Reply whether or not customer exists
			if (custAcc == null) {
				send("NAK");
				// Receive customer information
				String[] cInfo = recv().split("\n");
				newAcc = new Customer(custName[0], custName[1], cInfo[0], cInfo[1], cInfo[2]);
				custID = newAcc.getId();
				// Reply that customer account has been created
				send("ACK");
			} else {
				// Account exists, send ACK
				custID = custAcc.getId();
				send("ACK");
			}
		
			// Receive and parse loan information
			String[] lInfo = recv().split("\n");
			Loan loan = new Loan(custID, Double.parseDouble(lInfo[0]), lInfo[1]);
			// Reply that loan has been created,
			// and ask to finalize transaction
			send("ACK");
			
			// Receive ACK to finalize
			if (recv().equals("ACK")) {
				// Store data, send ACK
				if (custAcc == null) {
					b.storeAccount(newAcc);
				}
				b.storeLoan(custAcc, loan);
				send("ACK");
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
		
		private String recv() {
			try {
				return sock.recvMessage();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	@Override
	public String getServerName() {
		return this.bank;
	}
}

