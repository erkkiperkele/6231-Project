package Corba.BankServerPackage;

/** 
 * Helper class for : TransferException
 *  
 * @author OpenORB Compiler
 */ 
public class TransferExceptionHelper
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
     * Insert TransferException into an any
     * @param a an any
     * @param t TransferException value
     */
    public static void insert(org.omg.CORBA.Any a, TransferException t)
    {
        a.insert_Streamable(new TransferExceptionHolder(t));
    }

    /**
     * Extract TransferException from an any
     *
     * @param a an any
     * @return the extracted TransferException value
     */
    public static TransferException extract(org.omg.CORBA.Any a )
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
                if ( s instanceof TransferExceptionHolder)
                    return ( (TransferExceptionHolder) s ).value;
            }
            catch ( org.omg.CORBA.BAD_INV_ORDER ex )
            {
            }
            TransferExceptionHolder h = new TransferExceptionHolder( read( a.create_input_stream() ) );
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
     * Return the TransferException TypeCode
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
                _tc = orb.create_exception_tc( id(), "TransferException", _members );
                _working = false;
            }
        }
        return _tc;
    }

    /**
     * Return the TransferException IDL ID
     * @return an ID
     */
    public static String id()
    {
        return _id;
    }

    private final static String _id = "IDL:LoanManager/StartBankServer/TransferException:1.0";

    /**
     * Read TransferException from a marshalled stream
     * @param istream the input stream
     * @return the readed TransferException value
     */
    public static TransferException read(org.omg.CORBA.portable.InputStream istream)
    {
        TransferException new_one = new TransferException();

        if (!istream.read_string().equals(id()))
         throw new org.omg.CORBA.MARSHAL();
        new_one.message = istream.read_string();

        return new_one;
    }

    /**
     * Write TransferException into a marshalled stream
     * @param ostream the output stream
     * @param value TransferException value
     */
    public static void write(org.omg.CORBA.portable.OutputStream ostream, TransferException value)
    {
        ostream.write_string(id());
        ostream.write_string( value.message );
    }

}
