package dlms.client;

import java.util.ArrayList;
import java.util.Random;

import dlms.corba.AppException;
import dlms.corba.FrontEnd;
import shared.data.Bank;

public class TestRunner extends Client {
	private static final int THREADS_TO_RUN = 5;
	
	public TestRunner() {
		super();
		System.out.println("TestRunner created.");
		CreateTestThreads();
	}
	
	private void CreateTestThreads() {
		int numberOfRunningThreads = 0;
		ArrayList<TestThread> runningThreads = new ArrayList<TestThread>(THREADS_TO_RUN);
		while (numberOfRunningThreads < THREADS_TO_RUN) {
			// Pass the FE ior to a new thread
			runningThreads.add(new TestThread(server));
			// Remove terminated threads
			runningThreads.removeIf(a -> a.getState() == Thread.State.TERMINATED);
			numberOfRunningThreads = runningThreads.size();
		}
	}
}

class TestThread extends Thread {
	// For building random strings
	static final String AB = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static Random r = new Random();
	
	private FrontEnd fe;
	private int accountID = -1;
	private int loanID = -1;
	private String password;
	private String bank;
	
	public TestThread(FrontEnd fe) {
		this.fe = fe;
		this.start();
	}
	
	public void run() {
		// Initial state. No customerID or loanID associated
		// 5 possible routes:
		// 1. Create a new account, with valid information
		// 2. Create a new account, with invalid information
		// 3. Print information for a valid bank
		// 4. Print information for an invalid bank
		int choice = r.nextInt() % 4;
		switch(choice) {
		case 0:
			CreateValidAccount();
			break;
		case 1:
			CreateInvalidAccount();
			break;
		case 2:
			PrintValidInfo();
			break;
		case 3:
			PrintInvalidInfo();
			break;
		default: // Should never reach here
			break;
		}
	}

	private void CreateValidAccount() {
		// Generate account information
		String firstName = generateRandomString(8);
		String lastName = generateRandomString(8);
		String email = generateRandomString(5) + "@gmail.com";
		password = generateRandomString(7);
		String phone = "514-123-4567";
		bank = Bank.getBanks()[r.nextInt(Bank.getBanks().length)].toString();
		try {
			accountID = fe.openAccount(bank, firstName, lastName, email, phone, password);
		} catch (AppException e) {
			e.printStackTrace();
		}
		if (accountID > -1) {
			// Success, decide next method to call
			printMessage("SUCCESS: Created account " + accountID);
			// Choose next test to run
			switch(r.nextInt(5)) {
			case 0:
				CreateValidLoan();
				break;
			case 1:
				CreateInvalidLoan();
				break;
			case 2:
				CreateLoanAtInvalidBank();
				break;
			case 3:
				CreateLoanWithInvalidAccount();
				break;
			case 4:
				CreateLoanWithInsufficientCredit();
				break;
			default: // Should not reach here
				break;
			}
		}
		else {
			// Failure. Terminate
			printMessage("Unable to create new account");
		}
	}

	private void CreateInvalidAccount() {
		String firstName = generateRandomString(8);
		String lastName = generateRandomString(8);
		String email = generateRandomString(5) + "@gmail.com";
		password = generateRandomString(2); // INVALID
		String phone = "514-123-4567";
		bank = Bank.getBanks()[r.nextInt(Bank.getBanks().length)].toString();
		try {
			accountID = fe.openAccount(bank, firstName, lastName, email, phone, password);
		} catch (AppException e) {
			printMessage("CreateInvalidAccount(): Exception caught");
			return;
		}
		printMessage("CreateInvalidAccount(): No exception caught");		
	}

	private void CreateValidLoan() {
		try {
			loanID = fe.getLoan(bank, accountID, password, 100);
		} catch (AppException e) {
			printMessage("getLoan(): Unable to create Loan");
		}
		if (loanID > -1) {
			printMessage("SUCCESS: Acquired loan " + loanID);
			// TODO Choose next test to run
		}
		else {
			printMessage("FAILURE: Could not acquire loan");
		}
	}

	private void CreateLoanWithInsufficientCredit() {
		int amount = 1000000;
		try {
			loanID = fe.getLoan(bank, accountID, password, amount);
		} catch (AppException e) {
			printMessage("CreateLoanWithInsufficientCredit(): Exception caught");
			return;
		}
		printMessage("CreateLoanWithInsufficientCredit(): Exception not caught");
	}

	private void CreateLoanWithInvalidAccount() {
		try {
			loanID = fe.getLoan(bank, -1, password, 100);
		} catch (AppException e) {
			printMessage("CreateLoanWithInvalidAccount(): Exception caught");
			return;
		}
		printMessage("CreateLoanWithInvalidAccount(): Exception not caught");
	}

	private void CreateLoanAtInvalidBank() {
		String bank = generateRandomString(5);
		try {
			loanID = fe.getLoan(bank, accountID, password, 100);
		} catch (AppException e) {
			printMessage("CreateLoanAtInvalidBank(): Exception caught");
			return;
		}
		printMessage("CreateLoanAtInvalidBank(): Exception not caught");
	}

	private void CreateInvalidLoan() {
		try {
			loanID = fe.getLoan(bank, accountID, password.substring(1), 100);
		} catch (AppException e) {
			printMessage("CreateInvalidLoan(): Exception caught");
			return;
		}
		printMessage("CreateInvalidLoan(): Exception not caught");
	}

	private void PrintValidInfo() {
		String result = null;
		String bank = Bank.getBanks()[r.nextInt(Bank.getBanks().length)].toString();
		try {
			result = fe.printCustomerInfo(bank);
		} catch (AppException e) {
			e.printStackTrace();
		}
		if (result == null) {
			// Failure
			printMessage("printCustomerInfo(" + bank + ") failed");
		} else {
			printMessage("printCustomerInfo(" + bank + ") succeeded");
			printMessage(result);
		}
	}

	private void PrintInvalidInfo() {
		String bank = generateRandomString(5);
		try {
			fe.printCustomerInfo(bank);
		} catch (AppException e) {
			printMessage("PrintInvalidInfo(): Exception caught");
			return;
		}
		printMessage("PrintInvalidInfo(): No Exception caught");
	}
	
	private static String generateRandomString(int len) {
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; ++i) {
			sb.append(AB.charAt(r.nextInt(AB.length())));
		}
		return sb.toString();
	}
	
	private void printMessage(String msg) {
		System.out.println("Thread " + this.getId() + ": " + msg);
	}
}
