package DLMS;

/** 
 * Helper class for : BankServerInterface
 *  
 * @author OpenORB Compiler
 */ 
public class BankServerInterfaceHelper
{
    /**
     * Insert BankServerInterface into an any
     * @param a an any
     * @param t BankServerInterface value
     */
    public static void insert(org.omg.CORBA.Any a, DLMS.BankServerInterface t)
    {
        a.insert_Object(t , type());
    }

    /**
     * Extract BankServerInterface from an any
     *
     * @param a an any
     * @return the extracted BankServerInterface value
     */
    public static DLMS.BankServerInterface extract( org.omg.CORBA.Any a )
    {
        if ( !a.type().equivalent( type() ) )
        {
            throw new org.omg.CORBA.MARSHAL();
        }
        try
        {
            return DLMS.BankServerInterfaceHelper.narrow( a.extract_Object() );
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
     * Return the BankServerInterface TypeCode
     * @return a TypeCode
     */
    public static org.omg.CORBA.TypeCode type()
    {
        if (_tc == null) {
            org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init();
            _tc = orb.create_interface_tc( id(), "BankServerInterface" );
        }
        return _tc;
    }

    /**
     * Return the BankServerInterface IDL ID
     * @return an ID
     */
    public static String id()
    {
        return _id;
    }

    private final static String _id = "IDL:DLMS/BankServerInterface:1.0";

    /**
     * Read BankServerInterface from a marshalled stream
     * @param istream the input stream
     * @return the readed BankServerInterface value
     */
    public static DLMS.BankServerInterface read(org.omg.CORBA.portable.InputStream istream)
    {
        return(DLMS.BankServerInterface)istream.read_Object(DLMS._BankServerInterfaceStub.class);
    }

    /**
     * Write BankServerInterface into a marshalled stream
     * @param ostream the output stream
     * @param value BankServerInterface value
     */
    public static void write(org.omg.CORBA.portable.OutputStream ostream, DLMS.BankServerInterface value)
    {
        ostream.write_Object((org.omg.CORBA.portable.ObjectImpl)value);
    }

    /**
     * Narrow CORBA::Object to BankServerInterface
     * @param obj the CORBA Object
     * @return BankServerInterface Object
     */
    public static BankServerInterface narrow(org.omg.CORBA.Object obj)
    {
        if (obj == null)
            return null;
        if (obj instanceof BankServerInterface)
            return (BankServerInterface)obj;

        if (obj._is_a(id()))
        {
            _BankServerInterfaceStub stub = new _BankServerInterfaceStub();
            stub._set_delegate(((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate());
            return stub;
        }

        throw new org.omg.CORBA.BAD_PARAM();
    }

    /**
     * Unchecked Narrow CORBA::Object to BankServerInterface
     * @param obj the CORBA Object
     * @return BankServerInterface Object
     */
    public static BankServerInterface unchecked_narrow(org.omg.CORBA.Object obj)
    {
        if (obj == null)
            return null;
        if (obj instanceof BankServerInterface)
            return (BankServerInterface)obj;

        _BankServerInterfaceStub stub = new _BankServerInterfaceStub();
        stub._set_delegate(((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate());
        return stub;

    }

}
