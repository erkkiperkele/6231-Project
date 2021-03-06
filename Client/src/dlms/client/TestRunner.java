package dlms.client;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import dlms.corba.AppException;
import dlms.corba.FrontEnd;
import shared.data.Bank;

public class TestRunner extends Client {
	private static final int THREADS_TO_RUN = 5;
	
	public static void main(String[] args) {
		System.out.println("Starting tests");
		new TestRunner();
	}
	
	public TestRunner() {
		super();
		System.out.println("TestRunner created.");
		CreateTestThreads();
	}
	
	private void CreateTestThreads() {
		int numberOfRunningThreads = 0;
		ArrayList<TestThread> runningThreads = new ArrayList<TestThread>(THREADS_TO_RUN);
		while (numberOfRunningThreads < THREADS_TO_RUN) {
			// Pass the FE stub to a new thread
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
	private static SimpleDateFormat dateFmt = new SimpleDateFormat("dd/MM/yyyy");
	
	private FrontEnd fe;
	private int accountID = -1;
	private int loanID = -1;
	private String curDueDate;
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
		double choice = r.nextDouble();
		try {
			sleep(1000);
		} catch (InterruptedException e) {}
		// Skew tree descent in favor of creating valid accounts
		// This ensures that future tests are run
		if (choice < 15.0/18.0) {
			CreateValidAccount();
		} else if (choice < 16.0/18.0) {
			CreateInvalidAccount();
		} else if (choice < 17.0/18.0) {
			PrintValidInfo();
		} else {
			PrintInvalidInfo();
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
			success("Created account " + accountID);
			// Choose next test to run
			double choice = r.nextDouble();
			try {
				sleep(1000);
			} catch (InterruptedException e) {}
			if (choice < 9.0/14.0) {
				CreateValidLoan();
			} else if (choice < 10.0/14.0) {
				CreateInvalidLoan();
			} else if (choice < 11.0/14.0) {
				CreateLoanAtInvalidBank();
			} else if (choice < 12.0/14.0) {
				CreateLoanWithInvalidAccount();
			} else if (choice < 13.0/14.0){
				CreateLoanWithInsufficientCredit();
			} else {
				// Create duplicate account
				try {
					accountID = fe.openAccount(bank, firstName, lastName, email, phone, password);
				} catch (AppException e) {
					success("CreateAcountDuplicate(): Exception caught");
					return;
				}
				fail("CreateAccountDuplicate(): Exception not caught");
			}
		}
		else {
			// Failure. Terminate
			fail("CreateValidAccount(): Unable to create new account");
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
			success("CreateInvalidAccount(): Exception caught");
			return;
		}
		fail("CreateInvalidAccount(): No exception caught");		
	}

	private void CreateValidLoan() {
		try {
			loanID = fe.getLoan(bank, accountID, password, 100);
		} catch (AppException e) {
			fail("CreateValidLoan(): Exception caught");
		}
		if (loanID > -1) {
			success("CreateValidLoan(): Acquired loan " + loanID);
			Calendar c = Calendar.getInstance();
			c.add(Calendar.MONTH, 6);
			curDueDate = dateFmt.format(c.getTime());
			try {
				sleep(1000);
			} catch (InterruptedException e) {}
			// Choose next test to run
			switch(r.nextInt(8)) {
			case 0:
				DelayPaymentValid();
				break;
			case 1:
				DelayPaymentInvalid();
				break;
			case 2:
				DelayPaymentNoSuchLoan();
				break;
			case 3:
				TransferLoanValid();
				break;
			case 4:
				TransferLoanSameBank();
				break;
			case 5:
				TransferLoanInvalidBank();
				break;
			case 6:
				TransferLoanInvalidLoan();
				break;
			case 7: // Get duplicate loan
				try {
					loanID = fe.getLoan(bank, accountID, password, 100);
				} catch (AppException e) {
					success("CreateLoanDuplicate(): Exception caught");
					return;
				}
				fail("CreateLoanDuplicate(): Exception not caught");
				return;
			default:
				break;
			}
		}
		else {
			fail("GetValidLoan(): Could not acquire loan");
		}
	}

	private void CreateInvalidLoan() {
		try {
			loanID = fe.getLoan(bank, accountID, password.substring(1), 100);
		} catch (AppException e) {
			success("CreateInvalidLoan(): Exception caught");
			return;
		}
		fail("CreateInvalidLoan(): Exception not caught");
	}

	private void CreateLoanWithInsufficientCredit() {
		int amount = 1000000;
		try {
			loanID = fe.getLoan(bank, accountID, password, amount);
		} catch (AppException e) {
			success("CreateLoanWithInsufficientCredit(): Exception caught");
			return;
		}
		fail("CreateLoanWithInsufficientCredit(): Exception not caught");
	}

	private void CreateLoanWithInvalidAccount() {
		try {
			loanID = fe.getLoan(bank, -1, password, 100);
		} catch (AppException e) {
			success("CreateLoanWithInvalidAccount(): Exception caught");
			return;
		}
		fail("CreateLoanWithInvalidAccount(): Exception not caught");
	}

	private void CreateLoanAtInvalidBank() {
		String bank = generateRandomString(5);
		try {
			loanID = fe.getLoan(bank, accountID, password, 100);
		} catch (AppException e) {
			success("CreateLoanAtInvalidBank(): Exception caught");
			return;
		}
		fail("CreateLoanAtInvalidBank(): Exception not caught");
	}

	private void TransferLoanInvalidLoan() {
		int index = 0;
		while (!Bank.getBanks()[index].toString().equals(bank)) {
			++index;
		}
		try {
			fe.transferLoan(bank, -1, bank, Bank.getBanks()[index + 1 % Bank.getBanks().length].toString());
		} catch (AppException e) {
			success("TransferLoanInvalidLoan(): Exception caught");
			return;
		}
		fail("TransferLoanInvalidLoan(): Exception not caught");
	}

	private void TransferLoanInvalidBank() {
		try {
			fe.transferLoan(bank, loanID, bank, generateRandomString(5));
		} catch (AppException e) {
			success("TransferLoanInvalidBank(): Exception caught");
			return;
		}
		fail("TransferLoanInvalidBank(): Exception not caught");
	}

	private void TransferLoanSameBank() {
		try {
			fe.transferLoan(bank, loanID, bank, bank);
		} catch (AppException e) {
			success("TransferLoanSameBank(): Exception caught");
			return;
		}
		fail("TransferLoanSameBank(): Exception not caught");		
	}

	private void TransferLoanValid() {
		int index = 0;
		long result = -1;
		while (!Bank.getBanks()[index].toString().equals(bank)) {
			++index;
		}
		String newBank = Bank.getBanks()[index + 1 % Bank.getBanks().length].toString();
		try {
			result = fe.transferLoan(bank, -1, bank, newBank);
		} catch (AppException e) {
			fail("TransferLoanValid(): Exception caught");
			return;
		}
		if (result > -1) {
			success("Transferred " + bank + " loan " + loanID + " to " + newBank + " loan " + result);
		} else {
			fail("Loan transfer failed");
		}
	}

	private void DelayPaymentValid() {
		Boolean result = null;
		try {
			result = fe.delayPayment(bank, loanID, curDueDate, "31/12/2017");
		}
		catch (AppException e) {
			fail("DelayPaymentValid(): Exception caught");
		}
		if (result == null || !result) {
			fail("DelayPaymentValid(): Delay payment failed");
		} else {
			success("Loan payment delayed successfully");
		}
	}

	private void DelayPaymentInvalid() {
		try {
			fe.delayPayment(bank, loanID, curDueDate, "01/01/1900");
		} catch (AppException e) {
			success("DelayPaymentInvalid(): Exception caught");
			return;
		}
		fail("DelayPaymentInvalid(): Exception not caught");
	}

	private void DelayPaymentNoSuchLoan() {
		try {
			fe.delayPayment(bank, -1, curDueDate, "31/12/2017");
		} catch (AppException e) {
			success("DelayPaymentNoSuchLoan(): Exception caught");
			return;
		}
		fail("DelayPaymentNoSuchLoan(): Exception not caught");
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
			fail("printCustomerInfo(" + bank + ") failed");
		} else {
			success("printCustomerInfo(" + bank + ") succeeded");
			printMessage(result);
		}
	}

	private void PrintInvalidInfo() {
		String bank = generateRandomString(5);
		try {
			fe.printCustomerInfo(bank);
		} catch (AppException e) {
			success("PrintInvalidInfo(): Exception caught");
			return;
		}
		fail("PrintInvalidInfo(): No Exception caught");
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
	
	private void success(String msg) {
		printMessage("SUCCESS: " + msg);
	}
	
	private void fail(String msg) {
		printMessage("FAILURE: " + msg);
	}
}
