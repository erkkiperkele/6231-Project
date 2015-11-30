import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.omg.CORBA.ORB;

import DLMS.BankServerInterface;
import DLMS.BankServerInterfaceHelper;


public class ManagerClient {

	static BankServerInterface[] banks;
	static String[] banknames;
	static Scanner in = new Scanner(System.in);
	static ORB orb;
	
	public static void main(String[] args) {
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
			System.out.println("\t1. Delay payment due date on a loan.");
			System.out.println("\t2. Get bank information.");
			do {
				System.out.print("\nChoice: ");
				choice = in.nextInt();
			} while (choice < 0 || choice > 2 );

			if (choice == 0) {
				break;
			}
			
			switch (choice) {
			case 1:
				DelayLoanPrompt();
				break;
			case 2:
				BankInfoPrompt();
				break;
			default:
				continue;
			}
		}
	}
	
	public static void DelayLoanPrompt() {
		System.out.println("--- Delay Loan ---");
		System.out.println("Select the bank containing the loan you wish to delay:");
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
		
		String lid, cdd, ndd;
		in.nextLine();
		System.out.println("Enter Loan ID: ");
		lid = in.nextLine();
		System.out.println("Enter current due date (yyyy-mm-dd): ");
		cdd = in.nextLine();
		System.out.println("Enter new due date (yyyy-mm-dd): ");
		ndd = in.nextLine();
		
		System.out.println("Attempting to change loan repayment date.");
		String response = "";
		response = bInt.delayPayment(banknames[choice-1], lid, cdd, ndd);
		
		System.out.println(response);
	}
	
	public static void BankInfoPrompt() {
		System.out.println("--- Get Bank Information ---");
		System.out.println("Select which bank you want to view:");
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
		
		String response = "";
		response = bInt.printCustomerInfo(banknames[choice-1]);
		
		System.out.println(response);
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