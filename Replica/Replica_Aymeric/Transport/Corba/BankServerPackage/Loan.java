package Transport.Corba.BankServerPackage;

/**
 * Struct definition: Loan.
 * 
 * @author OpenORB Compiler
*/
public final class Loan implements org.omg.CORBA.portable.IDLEntity
{
    /**
     * Struct member loanNumber
     */
    public short loanNumber;

    /**
     * Struct member customerAccountNumber
     */
    public short customerAccountNumber;

    /**
     * Struct member amount
     */
    public int amount;

    /**
     * Struct member dueDate
     */
    public Transport.Corba.BankServerPackage.Date dueDate;

    /**
     * Default constructor
     */
    public Loan()
    { }

    /**
     * Constructor with fields initialization
     * @param loanNumber loanNumber struct member
     * @param customerAccountNumber customerAccountNumber struct member
     * @param amount amount struct member
     * @param dueDate dueDate struct member
     */
    public Loan(short loanNumber, short customerAccountNumber, int amount, Transport.Corba.BankServerPackage.Date dueDate)
    {
        this.loanNumber = loanNumber;
        this.customerAccountNumber = customerAccountNumber;
        this.amount = amount;
        this.dueDate = dueDate;
    }

}
