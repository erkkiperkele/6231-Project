package impl;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import shared.data.*;

public class BankStore {
	private Bank name;
	private HashMap<String, ArrayList<Customer>> customers;
	private HashMap<String, ArrayList<Loan>> loans;
	
	public BankStore(Bank name) {
		this.name = name;
		
		// Read accounts and loans info from files
		File facc = new File("./" + name + "/accounts.ser");
		File floan = new File("./" + name + "/loans.ser");
		if (facc.exists() && floan.exists()) {
			try {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(facc));
				this.customers = (HashMap<String, ArrayList<Customer>>)ois.readObject();

				ois = new ObjectInputStream(new FileInputStream(floan));
				this.loans = (HashMap<String, ArrayList<Loan>>)ois.readObject();
				ois.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			customers = new HashMap<String, ArrayList<Customer>>();
			loans = new HashMap<String, ArrayList<Loan>>();
			for (char i = 'A'; i <= 'Z'; ++i) {
				customers.put(String.valueOf(i), new ArrayList<Customer>());
				loans.put(String.valueOf(i), new ArrayList<Loan>());
			}
			try {
				new File("./" + name).mkdirs();
				new File("./" + name + "/logs").mkdir();
				facc.createNewFile();
				floan.createNewFile();
				SerializeAccounts();
				SerializeLoans();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Bank getName() {
		return name;
	}
	
	public Loan getLoan(int id) {
		return this.loans.values()
				.stream()
				.flatMap(l -> l.stream())
				.filter(l -> l.getLoanNumber() == id)
				.findFirst()
				.orElse(null);
	}
	
	public void refuseLoan(Customer c, double amount) {
		synchronized (this) {
			try {
				FileWriter fout = new FileWriter("./" + name + "/logs/" + c.getId() + ".txt", true);
				fout.write(getTimestamp() + " :: " + "Applied for, and was refused a $" + amount + "loan.\n");
				fout.close();
				SerializeLoans();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
	}
	
	public void changeLoanRepayment(Loan l, Date ndd) {
		Date cdd = l.getDueDate();
		
		synchronized (l) {
			l.setDueDate(ndd);
		}
		synchronized (this) {
			try {
				File f = new File("./" + name + "/logs/Manager.txt");
				if (!f.exists()) f.createNewFile();
				FileWriter fout = new FileWriter(f, true);
				fout.write(getTimestamp() + " :: Manager changed loan " + l.getLoanNumber() + "\n");
				fout.write("\t Old: " + cdd + ". New: " + ndd + ".\n");
				fout.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Loan getLoanByCustomer(int can) {
		return this.loans.values()
				.stream()
				.flatMap(l -> l.stream())
				.filter(l -> l.getCustomerAccountNumber() == can)
				.findFirst()
				.orElse(null);
	}
	
	public Customer getCustomer(int id) {
		return this.customers.values()
                .stream()
                .flatMap(a -> a.stream())
                .filter(a -> a.getId() == id)
                .findFirst()
                .orElse(null);
	}
	
	public Customer getCustomer(String fn, String ln) {
		return this.customers.get(fn.charAt(0))
				.stream()
				.filter(a -> a.getFirstName().equals(fn) && a.getLastName().equals(ln))
				.findFirst()
				.orElse(null);
	}
	
	public void storeCustomer(Customer account) {
		String fn = account.getFirstName().substring(0,1);
		synchronized(customers.get(fn)) {
			customers.get(fn).add(account);
		}
		synchronized (this) {
			try {
				File f = new File("./" + name + "/logs/" + account.getId() + ".txt");
				f.createNewFile();
				FileWriter fout = new FileWriter(f, true);
				fout.write(getTimestamp() + " :: " + "Account created.\n");
				fout.write(account.toString());
				fout.close();
				SerializeAccounts();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void storeLoan(Customer c, Loan loan) {
		String fn = c.getFirstName().substring(0,1);
		if (loans.get(fn) == null) {
			loans.put(String.valueOf(fn), new ArrayList<Loan>());
		}
		synchronized(loans.get(fn)) {
			loans.get(fn).add(loan);
		}
		synchronized (this) {
			try {
				FileWriter fout = new FileWriter("./" + name + "/logs/" + loan.getCustomerAccountNumber() + ".txt", true);
				fout.write(getTimestamp() + " :: " + "Successfully took out loan.\n");
				fout.write(loan.toString());
				fout.close();
				SerializeLoans();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void removeLoan(int id) {
		if (((Collection<Loan>) loans.values()
				.stream()
				.flatMap(l -> l.stream()))
				.removeIf((Loan l) -> l.getLoanNumber() == id)) {
			synchronized(this) {
				SerializeLoans();
			}
		}
	}

	private String getTimestamp() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd : HH:mm:ss");
		return df.format(new Date());
	}
	
	public String printInfo() {
		
		try {
			File f = new File("./" + name + "/logs/Manager.txt");
			if (!f.exists()) f.createNewFile();
			FileWriter fout = new FileWriter(f, true);
			fout.write(getTimestamp() + " :: Manager requested bank info.\n");
			fout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		StringWriter sw = new StringWriter();
		// Write CustomerAccount Information
		Collection<ArrayList<Customer>> as = customers.values();
		Iterator<ArrayList<Customer>> ait = as.iterator();
		while (ait.hasNext()) {
			Iterator<Customer> cit = ait.next().iterator();
			while (cit.hasNext()) {
				Customer c = cit.next();
				synchronized(c) {
					sw.write(c.toString());
				}
			}
		}
		
		// Write Loan Information
		Collection<ArrayList<Loan>> ls = loans.values();
		Iterator<ArrayList<Loan>> lsit = ls.iterator();
		while (lsit.hasNext()) {
			Iterator<Loan> lit = lsit.next().iterator();
			while (lit.hasNext()) {
				Loan l = lit.next();
				synchronized(l) {
					sw.write(l.toString());
				}
			}
		}
		
		return sw.toString();
	}
	
	public List<Loan> getLoanState() {
		return loans.values()
				.stream()
				.flatMap(a -> a.stream())
				.collect(Collectors.toList());
	}
	
	public List<Customer> getCustomerState() {
		return customers.values()
				.stream()
				.flatMap(a -> a.stream())
				.collect(Collectors.toList());
	}
	
	public void setCurrentState(BankState state) {
		setCustomers(state.getCustomerList());
		setLoans(state.getLoanList());
	}
	
	private void setLoans(List<Loan> list) {
		loans = new HashMap<String, ArrayList<Loan>>();
		list.stream().forEach(a -> storeLoan(getCustomer(a.getCustomerAccountNumber()), a));
	}

	private void setCustomers(List<Customer> list) {
		customers = new HashMap<String, ArrayList<Customer>>();
		list.stream().forEach(a -> storeCustomer(a));
	}
	
	private void SerializeAccounts() {
		try {
			FileOutputStream fout = new FileOutputStream("./" + name + "/accounts.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(customers);
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void SerializeLoans() {
		try {
			FileOutputStream fout = new FileOutputStream("./" + name + "/loans.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(loans);
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
