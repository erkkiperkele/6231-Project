package Transport.Corba.BankServerPackage;

/** 
 * Helper class for : BankInfo
 *  
 * @author OpenORB Compiler
 */ 
public class BankInfoHelper
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
     * Insert BankInfo into an any
     * @param a an any
     * @param t BankInfo value
     */
    public static void insert(org.omg.CORBA.Any a, Transport.Corba.BankServerPackage.BankInfo t)
    {
        a.insert_Streamable(new Transport.Corba.BankServerPackage.BankInfoHolder(t));
    }

    /**
     * Extract BankInfo from an any
     *
     * @param a an any
     * @return the extracted BankInfo value
     */
    public static Transport.Corba.BankServerPackage.BankInfo extract( org.omg.CORBA.Any a )
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
                if ( s instanceof Transport.Corba.BankServerPackage.BankInfoHolder )
                    return ( ( Transport.Corba.BankServerPackage.BankInfoHolder ) s ).value;
            }
            catch ( org.omg.CORBA.BAD_INV_ORDER ex )
            {
            }
            Transport.Corba.BankServerPackage.BankInfoHolder h = new Transport.Corba.BankServerPackage.BankInfoHolder( read( a.create_input_stream() ) );
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
     * Return the BankInfo TypeCode
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
                _members[ 0 ].name = "customersInfo";
                _members[ 0 ].type = orb.create_sequence_tc( 0, Transport.Corba.BankServerPackage.CustomerInfoHelper.type() );
                _tc = orb.create_struct_tc( id(), "BankInfo", _members );
                _working = false;
            }
        }
        return _tc;
    }

    /**
     * Return the BankInfo IDL ID
     * @return an ID
     */
    public static String id()
    {
        return _id;
    }

    private final static String _id = "IDL:LoanManager/BankServer/BankInfo:1.0";

    /**
     * Read BankInfo from a marshalled stream
     * @param istream the input stream
     * @return the readed BankInfo value
     */
    public static Transport.Corba.BankServerPackage.BankInfo read(org.omg.CORBA.portable.InputStream istream)
    {
        Transport.Corba.BankServerPackage.BankInfo new_one = new Transport.Corba.BankServerPackage.BankInfo();

        {
        int size7 = istream.read_ulong();
        new_one.customersInfo = new Transport.Corba.BankServerPackage.CustomerInfo[size7];
        for (int i7=0; i7<new_one.customersInfo.length; i7++)
         {
            new_one.customersInfo[i7] = Transport.Corba.BankServerPackage.CustomerInfoHelper.read(istream);

         }
        }

        return new_one;
    }

    /**
     * Write BankInfo into a marshalled stream
     * @param ostream the output stream
     * @param value BankInfo value
     */
    public static void write(org.omg.CORBA.portable.OutputStream ostream, Transport.Corba.BankServerPackage.BankInfo value)
    {
        ostream.write_ulong( value.customersInfo.length );
        for ( int i7 = 0; i7 < value.customersInfo.length; i7++ )
        {
            Transport.Corba.BankServerPackage.CustomerInfoHelper.write( ostream, value.customersInfo[ i7 ] );

        }
    }

}
