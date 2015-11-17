package Transport.Corba.LoanManager;

/**
 * Interface definition: BankServer.
 * 
 * @author OpenORB Compiler
 */
public class _BankServerStub extends org.omg.CORBA.portable.ObjectImpl
        implements BankServer
{
    static final String[] _ids_list =
    {
        "IDL:LoanManager/BankServer:1.0"
    };

    public String[] _ids()
    {
     return _ids_list;
    }

    private final static Class _opsClass = Transport.Corba.LoanManager.BankServerOperations.class;

    /**
     * Operation openAccount
     */
    public short openAccount(Transport.Corba.BankServerPackage.Bank bank, String firstName, String lastName, String emailAddress, String phoneNumber, String password)
    {
        while(true)
        {
            if (!this._is_local())
            {
                org.omg.CORBA.portable.InputStream _input = null;
                try
                {
                    org.omg.CORBA.portable.OutputStream _output = this._request("openAccount",true);
                    Transport.Corba.BankServerPackage.BankHelper.write(_output,bank);
                    _output.write_string(firstName);
                    _output.write_string(lastName);
                    _output.write_string(emailAddress);
                    _output.write_string(phoneNumber);
                    _output.write_string(password);
                    _input = this._invoke(_output);
                    short _arg_ret = _input.read_short();
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
                Transport.Corba.LoanManager.BankServerOperations _self = (Transport.Corba.LoanManager.BankServerOperations) _so.servant;
                try
                {
                    return _self.openAccount( bank,  firstName,  lastName,  emailAddress,  phoneNumber,  password);
                }
                finally
                {
                    _servant_postinvoke(_so);
                }
            }
        }
    }

    /**
     * Operation getCustomer
     */
    public Transport.Corba.BankServerPackage.Customer getCustomer(Transport.Corba.BankServerPackage.Bank bank, String email, String password)
        throws Transport.Corba.BankServerPackage.FailedLoginException
    {
        while(true)
        {
            if (!this._is_local())
            {
                org.omg.CORBA.portable.InputStream _input = null;
                try
                {
                    org.omg.CORBA.portable.OutputStream _output = this._request("getCustomer",true);
                    Transport.Corba.BankServerPackage.BankHelper.write(_output,bank);
                    _output.write_string(email);
                    _output.write_string(password);
                    _input = this._invoke(_output);
                    Transport.Corba.BankServerPackage.Customer _arg_ret = Transport.Corba.BankServerPackage.CustomerHelper.read(_input);
                    return _arg_ret;
                }
                catch(org.omg.CORBA.portable.RemarshalException _exception)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _exception)
                {
                    String _exception_id = _exception.getId();
                    if (_exception_id.equals(Transport.Corba.BankServerPackage.FailedLoginExceptionHelper.id()))
                    {
                        throw Transport.Corba.BankServerPackage.FailedLoginExceptionHelper.read(_exception.getInputStream());
                    }

                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: "+ _exception_id);
                }
                finally
                {
                    this._releaseReply(_input);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _so = _servant_preinvoke("getCustomer",_opsClass);
                if (_so == null)
                   continue;
                Transport.Corba.LoanManager.BankServerOperations _self = (Transport.Corba.LoanManager.BankServerOperations) _so.servant;
                try
                {
                    return _self.getCustomer(bank, email,  password);
                }
                finally
                {
                    _servant_postinvoke(_so);
                }
            }
        }
    }

    /**
     * Operation signIn
     */
    public Transport.Corba.BankServerPackage.Customer signIn(Transport.Corba.BankServerPackage.Bank bank, String email, String password)
        throws Transport.Corba.BankServerPackage.FailedLoginException
    {
        while(true)
        {
            if (!this._is_local())
            {
                org.omg.CORBA.portable.InputStream _input = null;
                try
                {
                    org.omg.CORBA.portable.OutputStream _output = this._request("signIn",true);
                    Transport.Corba.BankServerPackage.BankHelper.write(_output,bank);
                    _output.write_string(email);
                    _output.write_string(password);
                    _input = this._invoke(_output);
                    Transport.Corba.BankServerPackage.Customer _arg_ret = Transport.Corba.BankServerPackage.CustomerHelper.read(_input);
                    return _arg_ret;
                }
                catch(org.omg.CORBA.portable.RemarshalException _exception)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _exception)
                {
                    String _exception_id = _exception.getId();
                    if (_exception_id.equals(Transport.Corba.BankServerPackage.FailedLoginExceptionHelper.id()))
                    {
                        throw Transport.Corba.BankServerPackage.FailedLoginExceptionHelper.read(_exception.getInputStream());
                    }

                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: "+ _exception_id);
                }
                finally
                {
                    this._releaseReply(_input);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _so = _servant_preinvoke("signIn",_opsClass);
                if (_so == null)
                   continue;
                Transport.Corba.LoanManager.BankServerOperations _self = (Transport.Corba.LoanManager.BankServerOperations) _so.servant;
                try
                {
                    return _self.signIn(bank, email,  password);
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
    public Transport.Corba.BankServerPackage.Loan getLoan(Transport.Corba.BankServerPackage.Bank bankId, short accountNumber, String password, int loanAmount)
        throws Transport.Corba.BankServerPackage.FailedLoginException
    {
        while(true)
        {
            if (!this._is_local())
            {
                org.omg.CORBA.portable.InputStream _input = null;
                try
                {
                    org.omg.CORBA.portable.OutputStream _output = this._request("getLoan",true);
                    Transport.Corba.BankServerPackage.BankHelper.write(_output,bankId);
                    _output.write_short(accountNumber);
                    _output.write_string(password);
                    _output.write_long(loanAmount);
                    _input = this._invoke(_output);
                    Transport.Corba.BankServerPackage.Loan _arg_ret = Transport.Corba.BankServerPackage.LoanHelper.read(_input);
                    return _arg_ret;
                }
                catch(org.omg.CORBA.portable.RemarshalException _exception)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _exception)
                {
                    String _exception_id = _exception.getId();
                    if (_exception_id.equals(Transport.Corba.BankServerPackage.FailedLoginExceptionHelper.id()))
                    {
                        throw Transport.Corba.BankServerPackage.FailedLoginExceptionHelper.read(_exception.getInputStream());
                    }

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
                Transport.Corba.LoanManager.BankServerOperations _self = (Transport.Corba.LoanManager.BankServerOperations) _so.servant;
                try
                {
                    return _self.getLoan(bankId, accountNumber, password,  loanAmount);
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
    public void delayPayment(Transport.Corba.BankServerPackage.Bank bank, short loanID, Transport.Corba.BankServerPackage.Date currentDueDate, Transport.Corba.BankServerPackage.Date newDueDate)
        throws Transport.Corba.BankServerPackage.RecordNotFoundException
    {
        while(true)
        {
            if (!this._is_local())
            {
                org.omg.CORBA.portable.InputStream _input = null;
                try
                {
                    org.omg.CORBA.portable.OutputStream _output = this._request("delayPayment",true);
                    Transport.Corba.BankServerPackage.BankHelper.write(_output,bank);
                    _output.write_short(loanID);
                    Transport.Corba.BankServerPackage.DateHelper.write(_output,currentDueDate);
                    Transport.Corba.BankServerPackage.DateHelper.write(_output,newDueDate);
                    _input = this._invoke(_output);
                    return;
                }
                catch(org.omg.CORBA.portable.RemarshalException _exception)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _exception)
                {
                    String _exception_id = _exception.getId();
                    if (_exception_id.equals(Transport.Corba.BankServerPackage.RecordNotFoundExceptionHelper.id()))
                    {
                        throw Transport.Corba.BankServerPackage.RecordNotFoundExceptionHelper.read(_exception.getInputStream());
                    }

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
                Transport.Corba.LoanManager.BankServerOperations _self = (Transport.Corba.LoanManager.BankServerOperations) _so.servant;
                try
                {
                    _self.delayPayment( bank,  loanID,  currentDueDate,  newDueDate);
                    return;
                }
                finally
                {
                    _servant_postinvoke(_so);
                }
            }
        }
    }

    /**
     * Operation getCustomersInfo
     */
    public Transport.Corba.BankServerPackage.BankInfo getCustomersInfo(Transport.Corba.BankServerPackage.Bank bank)
        throws Transport.Corba.BankServerPackage.FailedLoginException
    {
        while(true)
        {
            if (!this._is_local())
            {
                org.omg.CORBA.portable.InputStream _input = null;
                try
                {
                    org.omg.CORBA.portable.OutputStream _output = this._request("getCustomersInfo",true);
                    Transport.Corba.BankServerPackage.BankHelper.write(_output,bank);
                    _input = this._invoke(_output);
                    Transport.Corba.BankServerPackage.BankInfo _arg_ret = Transport.Corba.BankServerPackage.BankInfoHelper.read(_input);
                    return _arg_ret;
                }
                catch(org.omg.CORBA.portable.RemarshalException _exception)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _exception)
                {
                    String _exception_id = _exception.getId();
                    if (_exception_id.equals(Transport.Corba.BankServerPackage.FailedLoginExceptionHelper.id()))
                    {
                        throw Transport.Corba.BankServerPackage.FailedLoginExceptionHelper.read(_exception.getInputStream());
                    }

                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: "+ _exception_id);
                }
                finally
                {
                    this._releaseReply(_input);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _so = _servant_preinvoke("getCustomersInfo",_opsClass);
                if (_so == null)
                   continue;
                Transport.Corba.LoanManager.BankServerOperations _self = (Transport.Corba.LoanManager.BankServerOperations) _so.servant;
                try
                {
                    return _self.getCustomersInfo( bank);
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
    public Transport.Corba.BankServerPackage.Loan transferLoan(short LoanId, Transport.Corba.BankServerPackage.Bank CurrentBank, Transport.Corba.BankServerPackage.Bank OtherBank)
        throws Transport.Corba.BankServerPackage.TransferException
    {
        while(true)
        {
            if (!this._is_local())
            {
                org.omg.CORBA.portable.InputStream _input = null;
                try
                {
                    org.omg.CORBA.portable.OutputStream _output = this._request("transferLoan",true);
                    _output.write_short(LoanId);
                    Transport.Corba.BankServerPackage.BankHelper.write(_output,CurrentBank);
                    Transport.Corba.BankServerPackage.BankHelper.write(_output,OtherBank);
                    _input = this._invoke(_output);
                    Transport.Corba.BankServerPackage.Loan _arg_ret = Transport.Corba.BankServerPackage.LoanHelper.read(_input);
                    return _arg_ret;
                }
                catch(org.omg.CORBA.portable.RemarshalException _exception)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _exception)
                {
                    String _exception_id = _exception.getId();
                    if (_exception_id.equals(Transport.Corba.BankServerPackage.TransferExceptionHelper.id()))
                    {
                        throw Transport.Corba.BankServerPackage.TransferExceptionHelper.read(_exception.getInputStream());
                    }

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
                Transport.Corba.LoanManager.BankServerOperations _self = (Transport.Corba.LoanManager.BankServerOperations) _so.servant;
                try
                {
                    return _self.transferLoan(LoanId, CurrentBank, OtherBank);
                }
                finally
                {
                    _servant_postinvoke(_so);
                }
            }
        }
    }

}
