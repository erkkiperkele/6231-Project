package Transport.Corba.BankServerPackage;

/**
 * Enum definition: Bank.
 *
 * @author OpenORB Compiler
*/
public final class Bank implements org.omg.CORBA.portable.IDLEntity
{
    /**
     * Enum member Royal value 
     */
    public static final int _Royal = 0;

    /**
     * Enum member Royal
     */
    public static final Bank Royal = new Bank(_Royal);

    /**
     * Enum member National value 
     */
    public static final int _National = 1;

    /**
     * Enum member National
     */
    public static final Bank National = new Bank(_National);

    /**
     * Enum member Dominion value 
     */
    public static final int _Dominion = 2;

    /**
     * Enum member Dominion
     */
    public static final Bank Dominion = new Bank(_Dominion);

    /**
     * Enum member None value 
     */
    public static final int _None = 3;

    /**
     * Enum member None
     */
    public static final Bank None = new Bank(_None);

    /**
     * Internal member value 
     */
    private final int _Bank_value;

    /**
     * Private constructor
     * @param  the enum value for this new member
     */
    private Bank( final int value )
    {
        _Bank_value = value;
    }

    /**
     * Maintains singleton property for serialized enums.
     * Issue 4271: IDL/Java issue, Mapping for IDL enum.
     */
    public java.lang.Object readResolve() throws java.io.ObjectStreamException
    {
        return from_int( value() );
    }

    /**
     * Return the internal member value
     * @return the member value
     */
    public int value()
    {
        return _Bank_value;
    }

    /**
     * Return a enum member from its value.
     * @param value An enum value
     * @return An enum member
         */
    public static Bank from_int( int value )
    {
        switch ( value )
        {
        case 0:
            return Royal;
        case 1:
            return National;
        case 2:
            return Dominion;
        case 3:
            return None;
        }
        throw new org.omg.CORBA.BAD_OPERATION();
    }

    /**
     * Return a string representation
     * @return a string representation of the enumeration
     */
    public java.lang.String toString()
    {
        switch ( _Bank_value )
        {
        case 0:
            return "Royal";
        case 1:
            return "National";
        case 2:
            return "Dominion";
        case 3:
            return "None";
        }
        throw new org.omg.CORBA.BAD_OPERATION();
    }

}
