package Transport.Corba.BankServerPackage;

/**
 * Struct definition: Date.
 * 
 * @author OpenORB Compiler
*/
public final class Date implements org.omg.CORBA.portable.IDLEntity
{
    /**
     * Struct member year
     */
    public int year;

    /**
     * Struct member month
     */
    public int month;

    /**
     * Struct member day
     */
    public int day;

    /**
     * Default constructor
     */
    public Date()
    { }

    /**
     * Constructor with fields initialization
     * @param year year struct member
     * @param month month struct member
     * @param day day struct member
     */
    public Date(int year, int month, int day)
    {
        this.year = year;
        this.month = month;
        this.day = day;
    }

}
