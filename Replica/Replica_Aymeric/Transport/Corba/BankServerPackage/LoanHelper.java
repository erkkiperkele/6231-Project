package Transport.Corba.BankServerPackage;

/** 
 * Helper class for : Loan
 *  
 * @author OpenORB Compiler
 */ 
public class LoanHelper
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
     * Insert Loan into an any
     * @param a an any
     * @param t Loan value
     */
    public static void insert(org.omg.CORBA.Any a, Transport.Corba.BankServerPackage.Loan t)
    {
        a.insert_Streamable(new Transport.Corba.BankServerPackage.LoanHolder(t));
    }

    /**
     * Extract Loan from an any
     *
     * @param a an any
     * @return the extracted Loan value
     */
    public static Transport.Corba.BankServerPackage.Loan extract( org.omg.CORBA.Any a )
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
                if ( s instanceof Transport.Corba.BankServerPackage.LoanHolder )
                    return ( ( Transport.Corba.BankServerPackage.LoanHolder ) s ).value;
            }
            catch ( org.omg.CORBA.BAD_INV_ORDER ex )
            {
            }
            Transport.Corba.BankServerPackage.LoanHolder h = new Transport.Corba.BankServerPackage.LoanHolder( read( a.create_input_stream() ) );
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
     * Return the Loan TypeCode
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
                org.omg.CORBA.StructMember _members[] = new org.omg.CORBA.StructMember[ 4 ];

                _members[ 0 ] = new org.omg.CORBA.StructMember();
                _members[ 0 ].name = "loanNumber";
                _members[ 0 ].type = orb.get_primitive_tc( org.omg.CORBA.TCKind.tk_short );
                _members[ 1 ] = new org.omg.CORBA.StructMember();
                _members[ 1 ].name = "customerAccountNumber";
                _members[ 1 ].type = orb.get_primitive_tc( org.omg.CORBA.TCKind.tk_short );
                _members[ 2 ] = new org.omg.CORBA.StructMember();
                _members[ 2 ].name = "amount";
                _members[ 2 ].type = orb.get_primitive_tc( org.omg.CORBA.TCKind.tk_long );
                _members[ 3 ] = new org.omg.CORBA.StructMember();
                _members[ 3 ].name = "dueDate";
                _members[ 3 ].type = Transport.Corba.BankServerPackage.DateHelper.type();
                _tc = orb.create_struct_tc( id(), "Loan", _members );
                _working = false;
            }
        }
        return _tc;
    }

    /**
     * Return the Loan IDL ID
     * @return an ID
     */
    public static String id()
    {
        return _id;
    }

    private final static String _id = "IDL:LoanManager/BankServer/Loan:1.0";

    /**
     * Read Loan from a marshalled stream
     * @param istream the input stream
     * @return the readed Loan value
     */
    public static Transport.Corba.BankServerPackage.Loan read(org.omg.CORBA.portable.InputStream istream)
    {
        Transport.Corba.BankServerPackage.Loan new_one = new Transport.Corba.BankServerPackage.Loan();

        new_one.loanNumber = istream.read_short();
        new_one.customerAccountNumber = istream.read_short();
        new_one.amount = istream.read_long();
        new_one.dueDate = Transport.Corba.BankServerPackage.DateHelper.read(istream);

        return new_one;
    }

    /**
     * Write Loan into a marshalled stream
     * @param ostream the output stream
     * @param value Loan value
     */
    public static void write(org.omg.CORBA.portable.OutputStream ostream, Transport.Corba.BankServerPackage.Loan value)
    {
        ostream.write_short( value.loanNumber );
        ostream.write_short( value.customerAccountNumber );
        ostream.write_long( value.amount );
        Transport.Corba.BankServerPackage.DateHelper.write( ostream, value.dueDate );
    }

}
