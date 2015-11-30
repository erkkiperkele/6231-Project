import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

import org.omg.CORBA.ORB;

import DLMS.BankServerInterface;
import DLMS.BankServerInterfaceHelper;


public class DLMSTester {
	static String[] banknames;
	static BankServerInterface[] banks;
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
		
		// Create and run test threads
		TestThread[] testThreads = new TestThread[banknames.length];
		for(int i = 0; i < testThreads.length; ++i) {
			testThreads[i] = new TestThread(i);
			testThreads[i].start();
		}
		
		for(int i = 0; i < testThreads.length; ++i) {
			try {
				testThreads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Test run completed");
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
	private static class TestThread extends Thread {
		private int offset;
		public TestThread(int offset) {
			this.offset = offset;
		}
		
		public void run() {
			BankServerInterface start = banks[offset];
			BankServerInterface b;
			int index;
			do {
				index = offset % banks.length;
				b = banks[index];
				// Create account + loan at bank
				String cid = b.openAccount(banknames[index], this.getName(), "Test", this.getName() + "@email", "555-555-1234", this.getName());
				System.out.println(this.getName() + ": Customer Account: " + cid);
				String lid = b.getLoan(banknames[index], cid, this.getName(), 1000.0);
				System.out.println(this.getName() + ": Loan ID: " + lid);
				// Transfer loan to another bank
				String transfer = b.transferLoan(lid, banknames[index], banknames[(index + 1) % banknames.length]);
				if (!transfer.equals("Loan successfully transferred.\n")) {
					System.out.println(this.getName() + ": Test failed. " + transfer);
					break;
				}
				System.out.println(this.getName() + ": Transfer from " + banknames[index] + " to " + banknames[(index + 1) % banknames.length]);
				++offset;
			} while (banks[offset % banks.length] != start);
		}
	}
}