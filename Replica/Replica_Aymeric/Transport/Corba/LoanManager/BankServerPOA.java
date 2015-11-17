package Transport.Corba.LoanManager;

/**
 * Interface definition: BankServer.
 * 
 * @author OpenORB Compiler
 */
public abstract class BankServerPOA extends org.omg.PortableServer.Servant
        implements BankServerOperations, org.omg.CORBA.portable.InvokeHandler
{
    public BankServer _this()
    {
        return BankServerHelper.narrow(_this_object());
    }

    public BankServer _this(org.omg.CORBA.ORB orb)
    {
        return BankServerHelper.narrow(_this_object(orb));
    }

    private static String [] _ids_list =
    {
        "IDL:LoanManager/BankServer:1.0"
    };

    public String[] _all_interfaces(org.omg.PortableServer.POA poa, byte [] objectId)
    {
        return _ids_list;
    }

    public final org.omg.CORBA.portable.OutputStream _invoke(final String opName,
            final org.omg.CORBA.portable.InputStream _is,
            final org.omg.CORBA.portable.ResponseHandler handler)
    {

        if (opName.equals("transferLoan")) {
                return _invoke_TransferLoan(_is, handler);
        } else if (opName.equals("delayPayment")) {
                return _invoke_delayPayment(_is, handler);
        } else if (opName.equals("getCustomer")) {
                return _invoke_getCustomer(_is, handler);
        } else if (opName.equals("getCustomersInfo")) {
                return _invoke_getCustomersInfo(_is, handler);
        } else if (opName.equals("getLoan")) {
                return _invoke_getLoan(_is, handler);
        } else if (opName.equals("openAccount")) {
                return _invoke_openAccount(_is, handler);
        } else if (opName.equals("signIn")) {
                return _invoke_signIn(_is, handler);
        } else {
            throw new org.omg.CORBA.BAD_OPERATION(opName);
        }
    }

    // helper methods
    private org.omg.CORBA.portable.OutputStream _invoke_openAccount(
            final org.omg.CORBA.portable.InputStream _is,
            final org.omg.CORBA.portable.ResponseHandler handler) {
        org.omg.CORBA.portable.OutputStream _output;
        Transport.Corba.BankServerPackage.Bank arg0_in = Transport.Corba.BankServerPackage.BankHelper.read(_is);
        String arg1_in = _is.read_string();
        String arg2_in = _is.read_string();
        String arg3_in = _is.read_string();
        String arg4_in = _is.read_string();
        String arg5_in = _is.read_string();

        short _arg_result = openAccount(arg0_in, arg1_in, arg2_in, arg3_in, arg4_in, arg5_in);

        _output = handler.createReply();
        _output.write_short(_arg_result);

        return _output;
    }

    private org.omg.CORBA.portable.OutputStream _invoke_getCustomer(
            final org.omg.CORBA.portable.InputStream _is,
            final org.omg.CORBA.portable.ResponseHandler handler) {
        org.omg.CORBA.portable.OutputStream _output;
        Transport.Corba.BankServerPackage.Bank arg0_in = Transport.Corba.BankServerPackage.BankHelper.read(_is);
        String arg1_in = _is.read_string();
        String arg2_in = _is.read_string();

        try
        {
            Transport.Corba.BankServerPackage.Customer _arg_result = getCustomer(arg0_in, arg1_in, arg2_in);

            _output = handler.createReply();
            Transport.Corba.BankServerPackage.CustomerHelper.write(_output,_arg_result);

        }
        catch (Transport.Corba.BankServerPackage.FailedLoginException _exception)
        {
            _output = handler.createExceptionReply();
            Transport.Corba.BankServerPackage.FailedLoginExceptionHelper.write(_output,_exception);
        }
        return _output;
    }

    private org.omg.CORBA.portable.OutputStream _invoke_signIn(
            final org.omg.CORBA.portable.InputStream _is,
            final org.omg.CORBA.portable.ResponseHandler handler) {
        org.omg.CORBA.portable.OutputStream _output;
        Transport.Corba.BankServerPackage.Bank arg0_in = Transport.Corba.BankServerPackage.BankHelper.read(_is);
        String arg1_in = _is.read_string();
        String arg2_in = _is.read_string();

        try
        {
            Transport.Corba.BankServerPackage.Customer _arg_result = signIn(arg0_in, arg1_in, arg2_in);

            _output = handler.createReply();
            Transport.Corba.BankServerPackage.CustomerHelper.write(_output,_arg_result);

        }
        catch (Transport.Corba.BankServerPackage.FailedLoginException _exception)
        {
            _output = handler.createExceptionReply();
            Transport.Corba.BankServerPackage.FailedLoginExceptionHelper.write(_output,_exception);
        }
        return _output;
    }

    private org.omg.CORBA.portable.OutputStream _invoke_getLoan(
            final org.omg.CORBA.portable.InputStream _is,
            final org.omg.CORBA.portable.ResponseHandler handler) {
        org.omg.CORBA.portable.OutputStream _output;
        Transport.Corba.BankServerPackage.Bank arg0_in = Transport.Corba.BankServerPackage.BankHelper.read(_is);
        short arg1_in = _is.read_short();
        String arg2_in = _is.read_string();
        int arg3_in = _is.read_long();

        try
        {
            Transport.Corba.BankServerPackage.Loan _arg_result = getLoan(arg0_in, arg1_in, arg2_in, arg3_in);

            _output = handler.createReply();
            Transport.Corba.BankServerPackage.LoanHelper.write(_output,_arg_result);

        }
        catch (Transport.Corba.BankServerPackage.FailedLoginException _exception)
        {
            _output = handler.createExceptionReply();
            Transport.Corba.BankServerPackage.FailedLoginExceptionHelper.write(_output,_exception);
        }
        return _output;
    }

    private org.omg.CORBA.portable.OutputStream _invoke_delayPayment(
            final org.omg.CORBA.portable.InputStream _is,
            final org.omg.CORBA.portable.ResponseHandler handler) {
        org.omg.CORBA.portable.OutputStream _output;
        Transport.Corba.BankServerPackage.Bank arg0_in = Transport.Corba.BankServerPackage.BankHelper.read(_is);
        short arg1_in = _is.read_short();
        Transport.Corba.BankServerPackage.Date arg2_in = Transport.Corba.BankServerPackage.DateHelper.read(_is);
        Transport.Corba.BankServerPackage.Date arg3_in = Transport.Corba.BankServerPackage.DateHelper.read(_is);

        try
        {
            delayPayment(arg0_in, arg1_in, arg2_in, arg3_in);

            _output = handler.createReply();

        }
        catch (Transport.Corba.BankServerPackage.RecordNotFoundException _exception)
        {
            _output = handler.createExceptionReply();
            Transport.Corba.BankServerPackage.RecordNotFoundExceptionHelper.write(_output,_exception);
        }
        return _output;
    }

    private org.omg.CORBA.portable.OutputStream _invoke_getCustomersInfo(
            final org.omg.CORBA.portable.InputStream _is,
            final org.omg.CORBA.portable.ResponseHandler handler) {
        org.omg.CORBA.portable.OutputStream _output;
        Transport.Corba.BankServerPackage.Bank arg0_in = Transport.Corba.BankServerPackage.BankHelper.read(_is);

        try
        {
            Transport.Corba.BankServerPackage.BankInfo _arg_result = getCustomersInfo(arg0_in);

            _output = handler.createReply();
            Transport.Corba.BankServerPackage.BankInfoHelper.write(_output,_arg_result);

        }
        catch (Transport.Corba.BankServerPackage.FailedLoginException _exception)
        {
            _output = handler.createExceptionReply();
            Transport.Corba.BankServerPackage.FailedLoginExceptionHelper.write(_output,_exception);
        }
        return _output;
    }

    private org.omg.CORBA.portable.OutputStream _invoke_TransferLoan(
            final org.omg.CORBA.portable.InputStream _is,
            final org.omg.CORBA.portable.ResponseHandler handler) {
        org.omg.CORBA.portable.OutputStream _output;
        short arg0_in = _is.read_short();
        Transport.Corba.BankServerPackage.Bank arg1_in = Transport.Corba.BankServerPackage.BankHelper.read(_is);
        Transport.Corba.BankServerPackage.Bank arg2_in = Transport.Corba.BankServerPackage.BankHelper.read(_is);

        try
        {
            Transport.Corba.BankServerPackage.Loan _arg_result = transferLoan(arg0_in, arg1_in, arg2_in);

            _output = handler.createReply();
            Transport.Corba.BankServerPackage.LoanHelper.write(_output,_arg_result);

        }
        catch (Transport.Corba.BankServerPackage.TransferException _exception)
        {
            _output = handler.createExceptionReply();
            Transport.Corba.BankServerPackage.TransferExceptionHelper.write(_output,_exception);
        }
        return _output;
    }

}
