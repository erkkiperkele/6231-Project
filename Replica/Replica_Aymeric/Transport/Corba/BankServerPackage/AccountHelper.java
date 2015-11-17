package Transport.Corba.BankServerPackage;

/** 
 * Helper class for : Account
 *  
 * @author OpenORB Compiler
 */ 
public class AccountHelper
{
    private static final boolean HAS_OPENORB;
    static
    {
        boolean hasOpenORB = false;
        try
        {
            Thread.currentThread().getContextClassLoader().loadClass( "org.openorb.orb.core.Any" );
            hasOpenORB = true;
        }
        catch ( ClassNotFoundException ex )
        {
            // do nothing
        }
        HAS_OPENORB = hasOpenORB;
    }
    /**
     * Insert Account into an any
     * @param a an any
     * @param t Account value
     */
    public static void insert(org.omg.CORBA.Any a, Transport.Corba.BankServerPackage.Account t)
    {
        a.insert_Streamable(new Transport.Corba.BankServerPackage.AccountHolder(t));
    }

    /**
     * Extract Account from an any
     *
     * @param a an any
     * @return the extracted Account value
     */
    public static Transport.Corba.BankServerPackage.Account extract( org.omg.CORBA.Any a )
    {
        if ( !a.type().equivalent( type() ) )
        {
            throw new org.omg.CORBA.MARSHAL();
        }
        if (HAS_OPENORB && a instanceof org.openorb.orb.core.Any) {
            // streamable extraction. The jdk stubs incorrectly define the Any stub
            org.openorb.orb.core.Any any = (org.openorb.orb.core.Any)a;
            try {
                org.omg.CORBA.portable.Streamable s = any.extract_Streamable();
                if ( s instanceof Transport.Corba.BankServerPackage.AccountHolder )
                    return ( ( Transport.Corba.BankServerPackage.AccountHolder ) s ).value;
            }
            catch ( org.omg.CORBA.BAD_INV_ORDER ex )
            {
            }
            Transport.Corba.BankServerPackage.AccountHolder h = new Transport.Corba.BankServerPackage.AccountHolder( read( a.create_input_stream() ) );
            a.insert_Streamable( h );
            return h.value;
        }
        return read( a.create_input_stream() );
    }

    //
    // Internal TypeCode value
    //
    private static org.omg.CORBA.TypeCode _tc = null;
    private static boolean _working = false;

    /**
     * Return the Account TypeCode
     * @return a TypeCode
     */
    public static org.omg.CORBA.TypeCode type()
    {
        if (_tc == null) {
            synchronized(org.omg.CORBA.TypeCode.class) {
                if (_tc != null)
                    return _tc;
                if (_working)
                    return org.omg.CORBA.ORB.init().create_recursive_tc(id());
                _working = true;
                org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init();
                org.omg.CORBA.StructMember _members[] = new org.omg.CORBA.StructMember[ 3 ];

                _members[ 0 ] = new org.omg.CORBA.StructMember();
                _members[ 0 ].name = "accountNumber";
                _members[ 0 ].type = orb.get_primitive_tc( org.omg.CORBA.TCKind.tk_short );
                _members[ 1 ] = new org.omg.CORBA.StructMember();
                _members[ 1 ].name = "owner";
                _members[ 1 ].type = Transport.Corba.BankServerPackage.CustomerHelper.type();
                _members[ 2 ] = new org.omg.CORBA.StructMember();
                _members[ 2 ].name = "creditLimit";
                _members[ 2 ].type = orb.get_primitive_tc( org.omg.CORBA.TCKind.tk_long );
                _tc = orb.create_struct_tc( id(), "Account", _members );
                _working = false;
            }
        }
        return _tc;
    }

    /**
     * Return the Account IDL ID
     * @return an ID
     */
    public static String id()
    {
        return _id;
    }

    private final static String _id = "IDL:LoanManager/BankServer/Account:1.0";

    /**
     * Read Account from a marshalled stream
     * @param istream the input stream
     * @return the readed Account value
     */
    public static Transport.Corba.BankServerPackage.Account read(org.omg.CORBA.portable.InputStream istream)
    {
        Transport.Corba.BankServerPackage.Account new_one = new Transport.Corba.BankServerPackage.Account();

        new_one.accountNumber = istream.read_short();
        new_one.owner = Transport.Corba.BankServerPackage.CustomerHelper.read(istream);
        new_one.creditLimit = istream.read_long();

        return new_one;
    }

    /**
     * Write Account into a marshalled stream
     * @param ostream the output stream
     * @param value Account value
     */
    public static void write(org.omg.CORBA.portable.OutputStream ostream, Transport.Corba.BankServerPackage.Account value)
    {
        ostream.write_short( value.accountNumber );
        Transport.Corba.BankServerPackage.CustomerHelper.write( ostream, value.owner );
        ostream.write_long( value.creditLimit );
    }

}
