package Corba.LoanManager;

/** 
 * Helper class for : StartBankServer
 *  
 * @author OpenORB Compiler
 */ 
public class BankServerHelper
{
    /**
     * Insert StartBankServer into an any
     * @param a an any
     * @param t StartBankServer value
     */
    public static void insert(org.omg.CORBA.Any a, BankServer t)
    {
        a.insert_Object(t , type());
    }

    /**
     * Extract StartBankServer from an any
     *
     * @param a an any
     * @return the extracted StartBankServer value
     */
    public static BankServer extract(org.omg.CORBA.Any a )
    {
        if ( !a.type().equivalent( type() ) )
        {
            throw new org.omg.CORBA.MARSHAL();
        }
        try
        {
            return BankServerHelper.narrow( a.extract_Object() );
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
     * Return the StartBankServer TypeCode
     * @return a TypeCode
     */
    public static org.omg.CORBA.TypeCode type()
    {
        if (_tc == null) {
            org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init();
            _tc = orb.create_interface_tc( id(), "StartBankServer" );
        }
        return _tc;
    }

    /**
     * Return the StartBankServer IDL ID
     * @return an ID
     */
    public static String id()
    {
        return _id;
    }

    private final static String _id = "IDL:LoanManager/StartBankServer:1.0";

    /**
     * Read StartBankServer from a marshalled stream
     * @param istream the input stream
     * @return the readed StartBankServer value
     */
    public static BankServer read(org.omg.CORBA.portable.InputStream istream)
    {
        return(BankServer)istream.read_Object(_BankServerStub.class);
    }

    /**
     * Write StartBankServer into a marshalled stream
     * @param ostream the output stream
     * @param value StartBankServer value
     */
    public static void write(org.omg.CORBA.portable.OutputStream ostream, BankServer value)
    {
        ostream.write_Object((org.omg.CORBA.portable.ObjectImpl)value);
    }

    /**
     * Narrow CORBA::Object to StartBankServer
     * @param obj the CORBA Object
     * @return StartBankServer Object
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
     * Unchecked Narrow CORBA::Object to StartBankServer
     * @param obj the CORBA Object
     * @return StartBankServer Object
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
