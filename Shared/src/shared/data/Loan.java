package shared.data;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("serial")
public class Loan implements Serializable {

    private int loanNumber;
    private int customerAccountNumber;
    private long amount;
    private Date dueDate;

    public Loan() {}
    
    public Loan(int loanNumber, int customerAccountNumber, long amount, Date dueDate) {
        this.loanNumber = loanNumber;
        this.customerAccountNumber = customerAccountNumber;
        this.amount = amount;
        this.dueDate = dueDate;
    }

    public int getLoanNumber() {
        return this.loanNumber;
    }

    public void setLoanNumber(int loanNumber) {
        this.loanNumber = loanNumber;
    }

    public int getCustomerAccountNumber() {
        return this.customerAccountNumber;
    }

    public void setCustomerAccountNumber(int customerAccountNumber) {
        this.customerAccountNumber = customerAccountNumber;
    }

    public long getAmount() {
        return this.amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public Date getDueDate() {
        return this.dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String toString() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String displayInfo = "";

        displayInfo += (this.loanNumber + " - ");
        displayInfo += (this.customerAccountNumber + " - ");
        displayInfo += (this.amount + " - ");
        displayInfo += (dateFormat.format(this.dueDate));

        return displayInfo;
    }
}
