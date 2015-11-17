package Transport.Corba.BankServerPackage;

/** 
 * Helper class for : RecordNotFoundException
 *  
 * @author OpenORB Compiler
 */ 
public class RecordNotFoundExceptionHelper
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
     * Insert RecordNotFoundException into an any
     * @param a an any
     * @param t RecordNotFoundException value
     */
    public static void insert(org.omg.CORBA.Any a, Transport.Corba.BankServerPackage.RecordNotFoundException t)
    {
        a.insert_Streamable(new Transport.Corba.BankServerPackage.RecordNotFoundExceptionHolder(t));
    }

    /**
     * Extract RecordNotFoundException from an any
     *
     * @param a an any
     * @return the extracted RecordNotFoundException value
     */
    public static Transport.Corba.BankServerPackage.RecordNotFoundException extract( org.omg.CORBA.Any a )
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
                if ( s instanceof Transport.Corba.BankServerPackage.RecordNotFoundExceptionHolder )
                    return ( ( Transport.Corba.BankServerPackage.RecordNotFoundExceptionHolder ) s ).value;
            }
            catch ( org.omg.CORBA.BAD_INV_ORDER ex )
            {
            }
            Transport.Corba.BankServerPackage.RecordNotFoundExceptionHolder h = new Transport.Corba.BankServerPackage.RecordNotFoundExceptionHolder( read( a.create_input_stream() ) );
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
     * Return the RecordNotFoundException TypeCode
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
                org.omg.CORBA.StructMember _members[] = new org.omg.CORBA.StructMember[ 1 ];

                _members[ 0 ] = new org.omg.CORBA.StructMember();
                _members[ 0 ].name = "message";
                _members[ 0 ].type = orb.get_primitive_tc( org.omg.CORBA.TCKind.tk_string );
                _tc = orb.create_exception_tc( id(), "RecordNotFoundException", _members );
                _working = false;
            }
        }
        return _tc;
    }

    /**
     * Return the RecordNotFoundException IDL ID
     * @return an ID
     */
    public static String id()
    {
        return _id;
    }

    private final static String _id = "IDL:LoanManager/BankServer/RecordNotFoundException:1.0";

    /**
     * Read RecordNotFoundException from a marshalled stream
     * @param istream the input stream
     * @return the readed RecordNotFoundException value
     */
    public static Transport.Corba.BankServerPackage.RecordNotFoundException read(org.omg.CORBA.portable.InputStream istream)
    {
        Transport.Corba.BankServerPackage.RecordNotFoundException new_one = new Transport.Corba.BankServerPackage.RecordNotFoundException();

        if (!istream.read_string().equals(id()))
         throw new org.omg.CORBA.MARSHAL();
        new_one.message = istream.read_string();

        return new_one;
    }

    /**
     * Write RecordNotFoundException into a marshalled stream
     * @param ostream the output stream
     * @param value RecordNotFoundException value
     */
    public static void write(org.omg.CORBA.portable.OutputStream ostream, Transport.Corba.BankServerPackage.RecordNotFoundException value)
    {
        ostream.write_string(id());
        ostream.write_string( value.message );
    }

}
