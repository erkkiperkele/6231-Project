package impl;

import shared.data.AbstractServerBank;
import shared.data.Bank;

/**
 * InitServers: Creates banks and binds BankServers to ports.
 * @author richard
 */

public class InitServers {
	public static void main(String[] args) {
		// Create BankServers
		AbstractServerBank[] bankservers = new BankServer[Bank.values().length];
		for (int i = 0; i < bankservers.length; ++i) {
			bankservers[i] = new BankServer(new BankStore(Bank.values()[i]));
		}
		
		System.out.println("Starting server");
	}
}
