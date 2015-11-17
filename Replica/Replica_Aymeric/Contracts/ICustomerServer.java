package Contracts;

import Data.Bank;
import Data.Customer;
import Data.Loan;
import Exceptions.TransferException;

import javax.security.auth.login.FailedLoginException;
import java.rmi.RemoteException;

/**
 * Defines the customer's server API contract (excludes any manager operation).
 */
public interface ICustomerServer {

    /**
     * Allows a client to create a new account
     * @param bank has to corresponds to the server's bank Identity
     * @param firstName
     * @param lastName
     * @param emailAddress
     * @param phoneNumber Format: 514.000.1111
     * @param password  No complexity validation performed at the moment on password
     * @return
     * @throws RemoteException Standard Remote interface exception. See RMI documentation.
     */
    int openAccount(Bank bank, String firstName, String lastName, String emailAddress, String phoneNumber, String password);

    /**
     * Allows to retrieve a single customer information
     * @param bank
     * @param email
     * @param password
     * @return
     * @throws RemoteException
     * @throws FailedLoginException thrown if the password and or the bank are incorrect.
     */
    Customer getCustomer(Bank bank, String email, String password) throws FailedLoginException;

    /**
     * Allows to validate a customer's credential on the server.
     * @param bank
     * @param email
     * @param password
     * @return
     * @throws RemoteException
     * @throws FailedLoginException
     */
    Customer signIn(Bank bank, String email, String password) throws FailedLoginException;

    /**
     * Will create a new loan for the given account after validating the customer's credit line against all banks.
     * (credit line should be inferior or equal to the customer's credit line at this bank, including the new loan)
     * @param bank
     * @param accountNumber
     * @param password
     * @param loanAmount
     * @return
     * @throws RemoteException
     * @throws FailedLoginException
     */
    Loan getLoan(Bank bank, int accountNumber, String password, long loanAmount) throws FailedLoginException;

    /**
     * Transfers a loan from a bank to another.
     * If the current user does not have an account at the other bank, the account will be created.
     * The transfer is transactional. If any of the required operation fails, all prior operations will
     * be rolled back.
     * @param loanId of the loan at the current bank
     * @param currentBank the current customer's bank where the loan is from
     * @param otherBank the new bank the loan is transferred to
     * @return the new loan created at the other bank
     * @throws TransferException
     */
    Loan transferLoan(int loanId, Bank currentBank, Bank otherBank) throws TransferException;
}
