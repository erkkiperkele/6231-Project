package dlms.corba;


/**
* dlms/corba/AppException.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from dlms.idl
* Wednesday, December 2, 2015 11:17:34 PM EST
*/

public final class AppException extends org.omg.CORBA.UserException
{

  public AppException ()
  {
    super(AppExceptionHelper.id());
  } // ctor


  public AppException (String $reason)
  {
    super(AppExceptionHelper.id() + "  " + $reason);
  } // ctor

} // class AppException
