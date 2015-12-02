package Corba.BankServerPackage;

/** 
 * Helper class for : Customer
 *  
 * @author OpenORB Compiler
 */ 
public class CustomerHelper
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
     * Insert Customer into an any
     * @param a an any
     * @param t Customer value
     */
    public static void insert(org.omg.CORBA.Any a, Customer t)
    {
        a.insert_Streamable(new CustomerHolder(t));
    }

    /**
     * Extract Customer from an any
     *
     * @param a an any
     * @return the extracted Customer value
     */
    public static Customer extract(org.omg.CORBA.Any a )
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
                if ( s instanceof CustomerHolder)
                    return ( (CustomerHolder) s ).value;
            }
            catch ( org.omg.CORBA.BAD_INV_ORDER ex )
            {
            }
            CustomerHolder h = new CustomerHolder( read( a.create_input_stream() ) );
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
     * Return the Customer TypeCode
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
                org.omg.CORBA.StructMember _members[] = new org.omg.CORBA.StructMember[ 8 ];

                _members[ 0 ] = new org.omg.CORBA.StructMember();
                _members[ 0 ].name = "id";
                _members[ 0 ].type = orb.get_primitive_tc( org.omg.CORBA.TCKind.tk_short );
                _members[ 1 ] = new org.omg.CORBA.StructMember();
                _members[ 1 ].name = "firstName";
                _members[ 1 ].type = orb.get_primitive_tc( org.omg.CORBA.TCKind.tk_string );
                _members[ 2 ] = new org.omg.CORBA.StructMember();
                _members[ 2 ].name = "lastName";
                _members[ 2 ].type = orb.get_primitive_tc( org.omg.CORBA.TCKind.tk_string );
                _members[ 3 ] = new org.omg.CORBA.StructMember();
                _members[ 3 ].name = "email";
                _members[ 3 ].type = orb.get_primitive_tc( org.omg.CORBA.TCKind.tk_string );
                _members[ 4 ] = new org.omg.CORBA.StructMember();
                _members[ 4 ].name = "bank";
                _members[ 4 ].type = BankHelper.type();
                _members[ 5 ] = new org.omg.CORBA.StructMember();
                _members[ 5 ].name = "accountNumber";
                _members[ 5 ].type = orb.get_primitive_tc( org.omg.CORBA.TCKind.tk_short );
                _members[ 6 ] = new org.omg.CORBA.StructMember();
                _members[ 6 ].name = "phone";
                _members[ 6 ].type = orb.get_primitive_tc( org.omg.CORBA.TCKind.tk_string );
                _members[ 7 ] = new org.omg.CORBA.StructMember();
                _members[ 7 ].name = "password";
                _members[ 7 ].type = orb.get_primitive_tc( org.omg.CORBA.TCKind.tk_string );
                _tc = orb.create_struct_tc( id(), "Customer", _members );
                _working = false;
            }
        }
        return _tc;
    }

    /**
     * Return the Customer IDL ID
     * @return an ID
     */
    public static String id()
    {
        return _id;
    }

    private final static String _id = "IDL:LoanManager/StartBankServer/Customer:1.0";

    /**
     * Read Customer from a marshalled stream
     * @param istream the input stream
     * @return the readed Customer value
     */
    public static Customer read(org.omg.CORBA.portable.InputStream istream)
    {
        Customer new_one = new Customer();

        new_one.id = istream.read_short();
        new_one.firstName = istream.read_string();
        new_one.lastName = istream.read_string();
        new_one.email = istream.read_string();
        new_one.bank = BankHelper.read(istream);
        new_one.accountNumber = istream.read_short();
        new_one.phone = istream.read_string();
        new_one.password = istream.read_string();

        return new_one;
    }

    /**
     * Write Customer into a marshalled stream
     * @param ostream the output stream
     * @param value Customer value
     */
    public static void write(org.omg.CORBA.portable.OutputStream ostream, Customer value)
    {
        ostream.write_short( value.id );
        ostream.write_string( value.firstName );
        ostream.write_string( value.lastName );
        ostream.write_string( value.email );
        BankHelper.write( ostream, value.bank );
        ostream.write_short( value.accountNumber );
        ostream.write_string( value.phone );
        ostream.write_string( value.password );
    }

}
