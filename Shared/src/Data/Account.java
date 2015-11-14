package Data;

import java.io.Serializable;

public class Account implements Serializable {

    private int accountNumber;
    private Customer owner;
    private long creditLimit;

    public Account(int accountNumber, Customer owner, long creditLimit) {
        this.accountNumber = accountNumber;
        this.owner = owner;
        this.creditLimit = creditLimit;
    }

    public Customer getOwner() {
        return this.owner;
    }

    public int getAccountNumber() {
        return this.accountNumber;
    }

    public long getCreditLimit() {
        return this.creditLimit;
    }
}
