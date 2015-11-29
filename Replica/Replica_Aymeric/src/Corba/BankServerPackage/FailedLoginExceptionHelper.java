package Corba.BankServerPackage;

/** 
 * Helper class for : FailedLoginException
 *  
 * @author OpenORB Compiler
 */ 
public class FailedLoginExceptionHelper
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
     * Insert FailedLoginException into an any
     * @param a an any
     * @param t FailedLoginException value
     */
    public static void insert(org.omg.CORBA.Any a, FailedLoginException t)
    {
        a.insert_Streamable(new FailedLoginExceptionHolder(t));
    }

    /**
     * Extract FailedLoginException from an any
     *
     * @param a an any
     * @return the extracted FailedLoginException value
     */
    public static FailedLoginException extract(org.omg.CORBA.Any a )
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
                if ( s instanceof FailedLoginExceptionHolder)
                    return ( (FailedLoginExceptionHolder) s ).value;
            }
            catch ( org.omg.CORBA.BAD_INV_ORDER ex )
            {
            }
            FailedLoginExceptionHolder h = new FailedLoginExceptionHolder( read( a.create_input_stream() ) );
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
     * Return the FailedLoginException TypeCode
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
                _tc = orb.create_exception_tc( id(), "FailedLoginException", _members );
                _working = false;
            }
        }
        return _tc;
    }

    /**
     * Return the FailedLoginException IDL ID
     * @return an ID
     */
    public static String id()
    {
        return _id;
    }

    private final static String _id = "IDL:LoanManager/BankServer/FailedLoginException:1.0";

    /**
     * Read FailedLoginException from a marshalled stream
     * @param istream the input stream
     * @return the readed FailedLoginException value
     */
    public static FailedLoginException read(org.omg.CORBA.portable.InputStream istream)
    {
        FailedLoginException new_one = new FailedLoginException();

        if (!istream.read_string().equals(id()))
         throw new org.omg.CORBA.MARSHAL();
        new_one.message = istream.read_string();

        return new_one;
    }

    /**
     * Write FailedLoginException into a marshalled stream
     * @param ostream the output stream
     * @param value FailedLoginException value
     */
    public static void write(org.omg.CORBA.portable.OutputStream ostream, FailedLoginException value)
    {
        ostream.write_string(id());
        ostream.write_string( value.message );
    }

}
