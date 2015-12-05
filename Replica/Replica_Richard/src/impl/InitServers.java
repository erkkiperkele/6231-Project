package impl;

import shared.data.AbstractServerBank;

/**
 * InitServers: Creates banks and binds BankServers to ports.
 * @author richard
 */

public class InitServers {
	public static void main(String[] args) {
		// Create 3 Bank objects
		Bank[] banks = new Bank[3];
		banks[0] = new Bank("TD Bank");
		banks[1] = new Bank("Bank of Montreal");
		banks[2] = new Bank("Scotia Bank");
		
		// Create 3 BankServers
		// See BankServer.java for UDP port information
		AbstractServerBank[] bankservers = new BankServer[banks.length];
		
		for(int i = 0; i < banks.length; ++i) {
			bankservers[i] = new BankServer(banks[i]);
		}
		
		System.out.println("Starting server");
	}
}
