package shared.data;

import shared.data.*;

import java.io.Serializable;

public class ReplicaState implements Serializable {

    private Account[] accounts;
    private Customer[] customers;
    private Loan[] loans;

    public ReplicaState(Account[] accounts, Customer[] customers, Loan[] loans) {
        this.accounts = accounts;
        this.customers = customers;
        this.loans = loans;
    }

    public Account[] getAccounts() {
        return accounts;
    }

    public Customer[] getCustomers() {
        return customers;
    }

    public Loan[] getLoans() {
        return loans;
    }
}
