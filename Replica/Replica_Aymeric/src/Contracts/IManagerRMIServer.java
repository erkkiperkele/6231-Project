package Contracts;

import Exceptions.RecordNotFoundException;

import javax.security.auth.login.FailedLoginException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;


/**
 * Defines the manager's server contract (excludes any customer's operation).
 */
public interface IManagerRMIServer extends Remote {

    /**
     * allows a manager to change the due date of a Loan
     * @param bank
     * @param loanID
     * @param currentDueDate
     * @param newDueDate At the moment there is no server side restriction regarding the new date.
     * @throws RemoteException
     * @throws RecordNotFoundException
     */
    void delayPayment(shared.data.Bank bank, int loanID, Date currentDueDate, Date newDueDate)
            throws RemoteException, RecordNotFoundException;

    /**
     * retrieves all customers information at the given bank
     * @param bank
     * @return
     * @throws RemoteException
     * @throws FailedLoginException thrown if the target bank does not correspond to the server's identity
     */
    shared.data.CustomerInfo[] getCustomersInfo(shared.data.Bank bank)
            throws RemoteException, FailedLoginException;


}
