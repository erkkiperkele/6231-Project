package Corba.BankServerPackage;

/**
 * Struct definition: BankInfo.
 * 
 * @author OpenORB Compiler
*/
public final class BankInfo implements org.omg.CORBA.portable.IDLEntity
{
    /**
     * Struct member customersInfo
     */
    public CustomerInfo[] customersInfo;

    /**
     * Default constructor
     */
    public BankInfo()
    { }

    /**
     * Constructor with fields initialization
     * @param customersInfo customersInfo struct member
     */
    public BankInfo(CustomerInfo[] customersInfo)
    {
        this.customersInfo = customersInfo;
    }

}
