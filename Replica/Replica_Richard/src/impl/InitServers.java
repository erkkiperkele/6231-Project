package impl;

import shared.data.AbstractServerBank;
import shared.data.Bank;
import shared.util.Constant;
import shared.util.Env;

/**
 * InitServers: Creates banks and binds BankServers to ports.
 * @author richard
 */

public class InitServers {
	public static void main(String[] args) {
		Env.setMachineName(Constant.MACHINE_NAME_RICHARD);
		// Create BankServers
		AbstractServerBank[] bankservers = new BankServer[Bank.values().length];
		for (int i = 0; i < bankservers.length; ++i) {
			bankservers[i] = new BankServer(new BankStore(Bank.values()[i]));
		}
		
		System.out.println("Starting server");
	}
}
