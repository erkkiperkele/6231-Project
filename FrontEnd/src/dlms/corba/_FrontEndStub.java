package dlms.corba;


/**
* dlms/corba/_FrontEndStub.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from dlms.idl
* Sunday, December 6, 2015 12:22:15 AM EST
*/

public class _FrontEndStub extends org.omg.CORBA.portable.ObjectImpl implements dlms.corba.FrontEnd
{

  public boolean delayPayment (String bankId, int loanId, String currentDueDate, String newDueDate) throws dlms.corba.AppException
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("delayPayment", true);
                $out.write_string (bankId);
                $out.write_long (loanId);
                $out.write_string (currentDueDate);
                $out.write_string (newDueDate);
                $in = _invoke ($out);
                boolean $result = $in.read_boolean ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:dlms/corba/AppException:1.0"))
                    throw dlms.corba.AppExceptionHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return delayPayment (bankId, loanId, currentDueDate, newDueDate        );
            } finally {
                _releaseReply ($in);
            }
  } // delayPayment

  public String printCustomerInfo (String bankId) throws dlms.corba.AppException
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("printCustomerInfo", true);
                $out.write_string (bankId);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:dlms/corba/AppException:1.0"))
                    throw dlms.corba.AppExceptionHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return printCustomerInfo (bankId        );
            } finally {
                _releaseReply ($in);
            }
  } // printCustomerInfo

  public int transferLoan (String bankId, int loanId, String currentBankId, String otherBankId) throws dlms.corba.AppException
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("transferLoan", true);
                $out.write_string (bankId);
                $out.write_long (loanId);
                $out.write_string (currentBankId);
                $out.write_string (otherBankId);
                $in = _invoke ($out);
                int $result = $in.read_long ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:dlms/corba/AppException:1.0"))
                    throw dlms.corba.AppExceptionHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return transferLoan (bankId, loanId, currentBankId, otherBankId        );
            } finally {
                _releaseReply ($in);
            }
  } // transferLoan

  public int openAccount (String bankId, String firstName, String lastName, String emailAddress, String phoneNumber, String password) throws dlms.corba.AppException
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("openAccount", true);
                $out.write_string (bankId);
                $out.write_string (firstName);
                $out.write_string (lastName);
                $out.write_string (emailAddress);
                $out.write_string (phoneNumber);
                $out.write_string (password);
                $in = _invoke ($out);
                int $result = $in.read_long ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:dlms/corba/AppException:1.0"))
                    throw dlms.corba.AppExceptionHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return openAccount (bankId, firstName, lastName, emailAddress, phoneNumber, password        );
            } finally {
                _releaseReply ($in);
            }
  } // openAccount

  public int getLoan (String bankId, int accountNbr, String password, int loanAmount) throws dlms.corba.AppException
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("getLoan", true);
                $out.write_string (bankId);
                $out.write_long (accountNbr);
                $out.write_string (password);
                $out.write_long (loanAmount);
                $in = _invoke ($out);
                int $result = $in.read_long ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:dlms/corba/AppException:1.0"))
                    throw dlms.corba.AppExceptionHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return getLoan (bankId, accountNbr, password, loanAmount        );
            } finally {
                _releaseReply ($in);
            }
  } // getLoan

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:dlms/corba/FrontEnd:1.0"};

  public String[] _ids ()
  {
    return (String[])__ids.clone ();
  }

  private void readObject (java.io.ObjectInputStream s) throws java.io.IOException
  {
     String str = s.readUTF ();
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init (args, props);
   try {
     org.omg.CORBA.Object obj = orb.string_to_object (str);
     org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl) obj)._get_delegate ();
     _set_delegate (delegate);
   } finally {
     orb.destroy() ;
   }
  }

  private void writeObject (java.io.ObjectOutputStream s) throws java.io.IOException
  {
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init (args, props);
   try {
     String str = orb.object_to_string (this);
     s.writeUTF (str);
   } finally {
     orb.destroy() ;
   }
  }
} // class _FrontEndStub
