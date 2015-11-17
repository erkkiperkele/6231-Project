package Transport.Corba.LoanManager;

/** 
 * Helper class for : BankServer
 *  
 * @author OpenORB Compiler
 */ 
public class BankServerHelper
{
    /**
     * Insert BankServer into an any
     * @param a an any
     * @param t BankServer value
     */
    public static void insert(org.omg.CORBA.Any a, Transport.Corba.LoanManager.BankServer t)
    {
        a.insert_Object(t , type());
    }

    /**
     * Extract BankServer from an any
     *
     * @param a an any
     * @return the extracted BankServer value
     */
    public static Transport.Corba.LoanManager.BankServer extract( org.omg.CORBA.Any a )
    {
        if ( !a.type().equivalent( type() ) )
        {
            throw new org.omg.CORBA.MARSHAL();
        }
        try
        {
            return Transport.Corba.LoanManager.BankServerHelper.narrow( a.extract_Object() );
        }
        catch ( final org.omg.CORBA.BAD_PARAM e )
        {
            throw new org.omg.CORBA.MARSHAL(e.getMessage());
        }
    }

    //
    // Internal TypeCode value
    //
    private static org.omg.CORBA.TypeCode _tc = null;

    /**
     * Return the BankServer TypeCode
     * @return a TypeCode
     */
    public static org.omg.CORBA.TypeCode type()
    {
        if (_tc == null) {
            org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init();
            _tc = orb.create_interface_tc( id(), "BankServer" );
        }
        return _tc;
    }

    /**
     * Return the BankServer IDL ID
     * @return an ID
     */
    public static String id()
    {
        return _id;
    }

    private final static String _id = "IDL:LoanManager/BankServer:1.0";

    /**
     * Read BankServer from a marshalled stream
     * @param istream the input stream
     * @return the readed BankServer value
     */
    public static Transport.Corba.LoanManager.BankServer read(org.omg.CORBA.portable.InputStream istream)
    {
        return(Transport.Corba.LoanManager.BankServer)istream.read_Object(Transport.Corba.LoanManager._BankServerStub.class);
    }

    /**
     * Write BankServer into a marshalled stream
     * @param ostream the output stream
     * @param value BankServer value
     */
    public static void write(org.omg.CORBA.portable.OutputStream ostream, Transport.Corba.LoanManager.BankServer value)
    {
        ostream.write_Object((org.omg.CORBA.portable.ObjectImpl)value);
    }

    /**
     * Narrow CORBA::Object to BankServer
     * @param obj the CORBA Object
     * @return BankServer Object
     */
    public static BankServer narrow(org.omg.CORBA.Object obj)
    {
        if (obj == null)
            return null;
        if (obj instanceof BankServer)
            return (BankServer)obj;

        if (obj._is_a(id()))
        {
            _BankServerStub stub = new _BankServerStub();
            stub._set_delegate(((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate());
            return stub;
        }

        throw new org.omg.CORBA.BAD_PARAM();
    }

    /**
     * Unchecked Narrow CORBA::Object to BankServer
     * @param obj the CORBA Object
     * @return BankServer Object
     */
    public static BankServer unchecked_narrow(org.omg.CORBA.Object obj)
    {
        if (obj == null)
            return null;
        if (obj instanceof BankServer)
            return (BankServer)obj;

        _BankServerStub stub = new _BankServerStub();
        stub._set_delegate(((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate());
        return stub;

    }

}
