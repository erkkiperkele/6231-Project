package impl;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.omg.CORBA.ORB;

import DLMS.BankServerInterfaceHelper;
import DLMS.BankServerInterface;

public class CustomerClient {

	static String[] banknames;
	static BankServerInterface[] banks;
	static Scanner in = new Scanner(System.in);

	public static void main(String[] args) {

		System.out.println("Welcome to the Distributed Loan Management System.");
		System.out.println("Please wait while the system initializes.");

		// Init ORB
		// Get banks with written-out IORs, and their names
		ORB orb = ORB.init(args, null);
		String[][] ior_tuples = getIORs();
		banks = new BankServerInterface[ior_tuples.length];
		banknames = new String[ior_tuples.length];
		for (int i = 0; i < ior_tuples.length; ++i) {
			org.omg.CORBA.Object o = orb.string_to_object(ior_tuples[i][0]);
			banks[i] = BankServerInterfaceHelper.narrow(o);
			banknames[i] = ior_tuples[i][1];
		}

		System.out.println("Located " + banknames.length + " registered banks:");
		for(int i = 0; i < banknames.length; ++i) {
			banknames[i] = banknames[i];
			System.out.println("\t" + banknames[i]);
		}

		// Input loop
		int choice = -1;

		while(true) {
			// Menu loop
			System.out.println("Select operation:");
			System.out.println("\t0. Quit application");
			System.out.println("\t1. Open account with a bank.");
			System.out.println("\t2. Get a loan with a bank.");
			System.out.println("\t3. Transfer loan to another bank.");
			do {
				System.out.print("\nChoice: ");
				choice = in.nextInt();
			} while (choice < 0 || choice > 3 );

			if (choice == 0) {
				break;
			}

			switch (choice) {
			case 1:
				OpenAccountPrompt();
				break;
			case 2:
				GetLoanPrompt();
				break;
			case 3:
				TransferLoanPrompt();
			default:
				continue;
			}
		}

		// Clean up and exit
		in.close();
		System.out.println("Thank you for using the Distributed Loan Management System!");
		System.exit(0);
	}

	public static void OpenAccountPrompt() {
		System.out.println("--- Open a new account ---");
		System.out.println("Select which bank you would like to open an account with:");
		System.out.println("\t0. Go back to main menu.");
		for(int i = 0; i < banknames.length; ++i) {
			System.out.println("\t" + (i + 1) + ". " + banknames[i]);
		}

		int choice = -1;
		do {
			System.out.print("\nChoice: ");
			choice = in.nextInt();
		} while (choice < 0 || choice > banknames.length);

		if (choice == 0) {
			return;
		}

		System.out.println("Selected " + banknames[choice-1] + ".");

		BankServerInterface bInt = banks[choice-1];

		String firstName, lastName, phoneNumber, emailAddress, password;
		System.out.print("Enter name: ");
		firstName = in.next();
		lastName = in.next();
		in.nextLine();
		System.out.print("Enter phone number: ");
		phoneNumber = in.nextLine();
		System.out.print("Enter email address: ");
		emailAddress = in.nextLine();
		do {
			System.out.print("Enter password (must be at least 6 characters): ");
			password = in.nextLine();
		} while (password.length() < 6);

		System.out.println("Attempting to create account with bank...");
		String accNum = null;
		accNum = bInt.openAccount(banknames[choice-1], firstName, lastName, emailAddress, phoneNumber, password);

		System.out.println("Account created: " + accNum);
	}

	public static void GetLoanPrompt() {
		System.out.println("--- Apply for a loan ---");
		System.out.println("Select which bank you would like to apply for a loan with:");
		System.out.println("\t0. Go back to main menu.");
		for(int i = 0; i < banknames.length; ++i) {
			System.out.println("\t" + (i + 1) + ". " + banknames[i]);
		}

		int choice = -1;

		do {
			System.out.print("\nChoice: ");
			choice = in.nextInt();
		} while (choice < 0 || choice > banknames.length);

		if (choice == 0) {
			return;
		}

		System.out.println("Selected " + banknames[choice-1] + ".");

		BankServerInterface bInt = banks[choice-1];

		double amount;
		String password;
		String accNum;

		in.nextLine();
		System.out.print("Please enter your account number: ");
		accNum = in.nextLine();
		System.out.print("Please enter your password: ");
		password = in.nextLine();
		System.out.print("Please enter the desired loan amount: ");
		amount = in.nextDouble();

		String response = null;

		response = bInt.getLoan(banknames[choice-1], accNum, password, amount);
		if (response == null) {
			System.out.println("Failed to create loan.");
		} else {
			System.out.println("Bank response: " + response);
		}
	}

	private static void TransferLoanPrompt() {
		in.nextLine();
		System.out.println("Please select the bank where the loan is held.");
		System.out.println("\t0. Go back to main menu.");
		for(int i = 0; i < banknames.length; ++i) {
			System.out.println("\t" + (i + 1) + ". " + banknames[i]);
		}

		int choice = -1;
		do {
			System.out.print("\nChoice: ");
			choice = in.nextInt();
		} while (choice < 0 || choice > banknames.length);

		if (choice == 0) {
			return;
		}

		System.out.println("Selected " + banknames[choice-1] + ".");

		BankServerInterface bInt = banks[choice-1];
		String sourceBank = banknames[choice-1];
		
		in.nextLine();
		System.out.print("Enter the Loan ID: ");
		String loanID = in.nextLine();
		
		System.out.println("Please select the bank where the loan should be transferred.");
		for(int i = 0; i < banknames.length; ++i) {
			if (banknames[i].equals(banknames[choice-1])) continue;
			System.out.println("\t" + (i + 1) + ". " + banknames[i]);
		}

		choice = -1;
		do {
			System.out.print("\nChoice: ");
			choice = in.nextInt();
		} while (choice < 0 || choice > banknames.length);

		if (choice == 0) {
			return;
		}
		
		System.out.println("Selected " + banknames[choice-1] + ".");
		String destBank = banknames[choice-1];
		
		String response = bInt.transferLoan(loanID, sourceBank, destBank);
		System.out.println("Response: " + response);
	}

	private static String[][] getIORs() {
		final Pattern p = Pattern.compile(".*_IOR.txt");
		File root = new File(".");
		
		File[] files = root.listFiles(new FileFilter() {
			@Override
			public boolean accept(File f) {
				return p.matcher(f.getName()).matches();
			}
		});
		String[][] out = new String[files.length][2];
		BufferedReader br = null;
		try {
			for (int i = 0; i < files.length; ++i) {
				br = new BufferedReader(new FileReader(files[i]));
				String n = files[i].getName();
				out[i][0] = br.readLine();
				out[i][1] = n.substring(0, n.length() - 8);
				br.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out;
	}
}
