package Transport.Corba.BankServerPackage;

/** 
 * Helper class for : CustomerInfo
 *  
 * @author OpenORB Compiler
 */ 
public class CustomerInfoHelper
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
     * Insert CustomerInfo into an any
     * @param a an any
     * @param t CustomerInfo value
     */
    public static void insert(org.omg.CORBA.Any a, Transport.Corba.BankServerPackage.CustomerInfo t)
    {
        a.insert_Streamable(new Transport.Corba.BankServerPackage.CustomerInfoHolder(t));
    }

    /**
     * Extract CustomerInfo from an any
     *
     * @param a an any
     * @return the extracted CustomerInfo value
     */
    public static Transport.Corba.BankServerPackage.CustomerInfo extract( org.omg.CORBA.Any a )
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
                if ( s instanceof Transport.Corba.BankServerPackage.CustomerInfoHolder )
                    return ( ( Transport.Corba.BankServerPackage.CustomerInfoHolder ) s ).value;
            }
            catch ( org.omg.CORBA.BAD_INV_ORDER ex )
            {
            }
            Transport.Corba.BankServerPackage.CustomerInfoHolder h = new Transport.Corba.BankServerPackage.CustomerInfoHolder( read( a.create_input_stream() ) );
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
     * Return the CustomerInfo TypeCode
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
                _members[ 0 ].name = "userName";
                _members[ 0 ].type = orb.get_primitive_tc( org.omg.CORBA.TCKind.tk_string );
                _members[ 1 ] = new org.omg.CORBA.StructMember();
                _members[ 1 ].name = "account";
                _members[ 1 ].type = Transport.Corba.BankServerPackage.AccountHelper.type();
                _members[ 2 ] = new org.omg.CORBA.StructMember();
                _members[ 2 ].name = "loans";
                _members[ 2 ].type = orb.create_sequence_tc( 0, Transport.Corba.BankServerPackage.LoanHelper.type() );
                _tc = orb.create_struct_tc( id(), "CustomerInfo", _members );
                _working = false;
            }
        }
        return _tc;
    }

    /**
     * Return the CustomerInfo IDL ID
     * @return an ID
     */
    public static String id()
    {
        return _id;
    }

    private final static String _id = "IDL:LoanManager/BankServer/CustomerInfo:1.0";

    /**
     * Read CustomerInfo from a marshalled stream
     * @param istream the input stream
     * @return the readed CustomerInfo value
     */
    public static Transport.Corba.BankServerPackage.CustomerInfo read(org.omg.CORBA.portable.InputStream istream)
    {
        Transport.Corba.BankServerPackage.CustomerInfo new_one = new Transport.Corba.BankServerPackage.CustomerInfo();

        new_one.userName = istream.read_string();
        new_one.account = Transport.Corba.BankServerPackage.AccountHelper.read(istream);
        {
        int size7 = istream.read_ulong();
        new_one.loans = new Transport.Corba.BankServerPackage.Loan[size7];
        for (int i7=0; i7<new_one.loans.length; i7++)
         {
            new_one.loans[i7] = Transport.Corba.BankServerPackage.LoanHelper.read(istream);

         }
        }

        return new_one;
    }

    /**
     * Write CustomerInfo into a marshalled stream
     * @param ostream the output stream
     * @param value CustomerInfo value
     */
    public static void write(org.omg.CORBA.portable.OutputStream ostream, Transport.Corba.BankServerPackage.CustomerInfo value)
    {
        ostream.write_string( value.userName );
        Transport.Corba.BankServerPackage.AccountHelper.write( ostream, value.account );
        ostream.write_ulong( value.loans.length );
        for ( int i7 = 0; i7 < value.loans.length; i7++ )
        {
            Transport.Corba.BankServerPackage.LoanHelper.write( ostream, value.loans[ i7 ] );

        }
    }

}
