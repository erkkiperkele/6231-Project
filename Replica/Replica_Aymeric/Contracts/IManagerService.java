package Contracts;

import Data.Bank;
import Data.CustomerInfo;
import Exceptions.RecordNotFoundException;

import javax.security.auth.login.FailedLoginException;
import java.util.Date;


/**
 * Defines the contract for the server's manager services.
 */
public interface IManagerService {

    /**
     * update a given loan with a new due date.
     * At the moment, there is no limitation regarding the new date.
     * @param bank currently, no verification is made on the provided bank
     * @param loanID
     * @param currentDueDate currently, no verification is made on the current due date of the loan
     * @param newDueDate
     * @throws RecordNotFoundException This exception is thrown if the loan couldn't be found for the given Id.
     */
    void delayPayment(Bank bank, int loanID, Date currentDueDate, Date newDueDate) throws RecordNotFoundException;

    /**
     * retrieves the all of the bank's customer information,
     * that is Accounts and Loans.
     * @param bank
     * @return
     * @throws FailedLoginException this exception is thrown in case the bank specified
     * doesn't correspond to the current server's bank
     */
    CustomerInfo[] getCustomersInfo(Bank bank) throws FailedLoginException;
}
