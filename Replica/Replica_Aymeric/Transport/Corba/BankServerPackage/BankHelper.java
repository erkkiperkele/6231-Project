package Transport.Corba.BankServerPackage;

/** 
 * Helper class for : Bank
 *  
 * @author OpenORB Compiler
 */ 
public class BankHelper
{
    /**
     * Insert Bank into an any
     * @param a an any
     * @param t Bank value
     */
    public static void insert(org.omg.CORBA.Any a, Transport.Corba.BankServerPackage.Bank t)
    {
        a.type(type());
        write(a.create_output_stream(),t);
    }

    /**
     * Extract Bank from an any
     *
     * @param a an any
     * @return the extracted Bank value
     */
    public static Transport.Corba.BankServerPackage.Bank extract( org.omg.CORBA.Any a )
    {
        if ( !a.type().equivalent( type() ) )
        {
            throw new org.omg.CORBA.MARSHAL();
        }
        return read( a.create_input_stream() );
    }

    //
    // Internal TypeCode value
    //
    private static org.omg.CORBA.TypeCode _tc = null;

    /**
     * Return the Bank TypeCode
     * @return a TypeCode
     */
    public static org.omg.CORBA.TypeCode type()
    {
        if (_tc == null) {
            org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init();
            String []_members = new String[ 4 ];
            _members[ 0 ] = "Royal";
            _members[ 1 ] = "National";
            _members[ 2 ] = "Dominion";
            _members[ 3 ] = "None";
            _tc = orb.create_enum_tc( id(), "Bank", _members );
        }
        return _tc;
    }

    /**
     * Return the Bank IDL ID
     * @return an ID
     */
    public static String id()
    {
        return _id;
    }

    private final static String _id = "IDL:LoanManager/BankServer/Bank:1.0";

    /**
     * Read Bank from a marshalled stream
     * @param istream the input stream
     * @return the readed Bank value
     */
    public static Transport.Corba.BankServerPackage.Bank read(org.omg.CORBA.portable.InputStream istream)
    {
        return Bank.from_int(istream.read_ulong());
    }

    /**
     * Write Bank into a marshalled stream
     * @param ostream the output stream
     * @param value Bank value
     */
    public static void write(org.omg.CORBA.portable.OutputStream ostream, Transport.Corba.BankServerPackage.Bank value)
    {
        ostream.write_ulong(value.value());
    }

}
