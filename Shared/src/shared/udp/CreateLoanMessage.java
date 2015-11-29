package shared.udp;

import shared.data.Account;
import shared.data.Loan;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CreateLoanMessage implements Serializable, IOperationMessage{

    private Account account;
    private Loan loan;

    public CreateLoanMessage(Account account, Loan loan) {
        this.account = account;
        this.loan = loan;
    }

    public Account getAccount() {
        return account;
    }

    public Loan getLoan() {
        return loan;
    }
}
