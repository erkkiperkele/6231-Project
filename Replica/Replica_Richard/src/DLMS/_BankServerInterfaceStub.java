package DLMS;

/**
 * Interface definition: BankServerInterface.
 * 
 * @author OpenORB Compiler
 */
public class _BankServerInterfaceStub extends org.omg.CORBA.portable.ObjectImpl
        implements BankServerInterface
{
    static final String[] _ids_list =
    {
        "IDL:DLMS/BankServerInterface:1.0"
    };

    public String[] _ids()
    {
     return _ids_list;
    }

    private final static Class _opsClass = DLMS.BankServerInterfaceOperations.class;

    /**
     * Operation openAccount
     */
    public String openAccount(String bank, String FirstName, String LastName, String EmailAddress, String PhoneNumber, String Password)
    {
        while(true)
        {
            if (!this._is_local())
            {
                org.omg.CORBA.portable.InputStream _input = null;
                try
                {
                    org.omg.CORBA.portable.OutputStream _output = this._request("openAccount",true);
                    _output.write_string(bank);
                    _output.write_string(FirstName);
                    _output.write_string(LastName);
                    _output.write_string(EmailAddress);
                    _output.write_string(PhoneNumber);
                    _output.write_string(Password);
                    _input = this._invoke(_output);
                    String _arg_ret = _input.read_string();
                    return _arg_ret;
                }
                catch(org.omg.CORBA.portable.RemarshalException _exception)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _exception)
                {
                    String _exception_id = _exception.getId();
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: "+ _exception_id);
                }
                finally
                {
                    this._releaseReply(_input);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _so = _servant_preinvoke("openAccount",_opsClass);
                if (_so == null)
                   continue;
                DLMS.BankServerInterfaceOperations _self = (DLMS.BankServerInterfaceOperations) _so.servant;
                try
                {
                    return _self.openAccount( bank,  FirstName,  LastName,  EmailAddress,  PhoneNumber,  Password);
                }
                finally
                {
                    _servant_postinvoke(_so);
                }
            }
        }
    }

    /**
     * Operation getLoan
     */
    public String getLoan(String Bank, String AccountNumber, String Password, double Amount)
    {
        while(true)
        {
            if (!this._is_local())
            {
                org.omg.CORBA.portable.InputStream _input = null;
                try
                {
                    org.omg.CORBA.portable.OutputStream _output = this._request("getLoan",true);
                    _output.write_string(Bank);
                    _output.write_string(AccountNumber);
                    _output.write_string(Password);
                    _output.write_double(Amount);
                    _input = this._invoke(_output);
                    String _arg_ret = _input.read_string();
                    return _arg_ret;
                }
                catch(org.omg.CORBA.portable.RemarshalException _exception)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _exception)
                {
                    String _exception_id = _exception.getId();
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: "+ _exception_id);
                }
                finally
                {
                    this._releaseReply(_input);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _so = _servant_preinvoke("getLoan",_opsClass);
                if (_so == null)
                   continue;
                DLMS.BankServerInterfaceOperations _self = (DLMS.BankServerInterfaceOperations) _so.servant;
                try
                {
                    return _self.getLoan( Bank,  AccountNumber,  Password,  Amount);
                }
                finally
                {
                    _servant_postinvoke(_so);
                }
            }
        }
    }

    /**
     * Operation delayPayment
     */
    public String delayPayment(String Bank, String LoanID, String CurrentDueDate, String NewDueDate)
    {
        while(true)
        {
            if (!this._is_local())
            {
                org.omg.CORBA.portable.InputStream _input = null;
                try
                {
                    org.omg.CORBA.portable.OutputStream _output = this._request("delayPayment",true);
                    _output.write_string(Bank);
                    _output.write_string(LoanID);
                    _output.write_string(CurrentDueDate);
                    _output.write_string(NewDueDate);
                    _input = this._invoke(_output);
                    String _arg_ret = _input.read_string();
                    return _arg_ret;
                }
                catch(org.omg.CORBA.portable.RemarshalException _exception)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _exception)
                {
                    String _exception_id = _exception.getId();
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: "+ _exception_id);
                }
                finally
                {
                    this._releaseReply(_input);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _so = _servant_preinvoke("delayPayment",_opsClass);
                if (_so == null)
                   continue;
                DLMS.BankServerInterfaceOperations _self = (DLMS.BankServerInterfaceOperations) _so.servant;
                try
                {
                    return _self.delayPayment( Bank,  LoanID,  CurrentDueDate,  NewDueDate);
                }
                finally
                {
                    _servant_postinvoke(_so);
                }
            }
        }
    }

    /**
     * Operation printCustomerInfo
     */
    public String printCustomerInfo(String Bank)
    {
        while(true)
        {
            if (!this._is_local())
            {
                org.omg.CORBA.portable.InputStream _input = null;
                try
                {
                    org.omg.CORBA.portable.OutputStream _output = this._request("printCustomerInfo",true);
                    _output.write_string(Bank);
                    _input = this._invoke(_output);
                    String _arg_ret = _input.read_string();
                    return _arg_ret;
                }
                catch(org.omg.CORBA.portable.RemarshalException _exception)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _exception)
                {
                    String _exception_id = _exception.getId();
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: "+ _exception_id);
                }
                finally
                {
                    this._releaseReply(_input);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _so = _servant_preinvoke("printCustomerInfo",_opsClass);
                if (_so == null)
                   continue;
                DLMS.BankServerInterfaceOperations _self = (DLMS.BankServerInterfaceOperations) _so.servant;
                try
                {
                    return _self.printCustomerInfo( Bank);
                }
                finally
                {
                    _servant_postinvoke(_so);
                }
            }
        }
    }

    /**
     * Operation transferLoan
     */
    public String transferLoan(String LoanID, String CurrentBank, String OtherBank)
    {
        while(true)
        {
            if (!this._is_local())
            {
                org.omg.CORBA.portable.InputStream _input = null;
                try
                {
                    org.omg.CORBA.portable.OutputStream _output = this._request("transferLoan",true);
                    _output.write_string(LoanID);
                    _output.write_string(CurrentBank);
                    _output.write_string(OtherBank);
                    _input = this._invoke(_output);
                    String _arg_ret = _input.read_string();
                    return _arg_ret;
                }
                catch(org.omg.CORBA.portable.RemarshalException _exception)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _exception)
                {
                    String _exception_id = _exception.getId();
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: "+ _exception_id);
                }
                finally
                {
                    this._releaseReply(_input);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _so = _servant_preinvoke("transferLoan",_opsClass);
                if (_so == null)
                   continue;
                DLMS.BankServerInterfaceOperations _self = (DLMS.BankServerInterfaceOperations) _so.servant;
                try
                {
                    return _self.transferLoan( LoanID,  CurrentBank,  OtherBank);
                }
                finally
                {
                    _servant_postinvoke(_so);
                }
            }
        }
    }

}
