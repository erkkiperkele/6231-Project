package Contracts;

import Data.Account;
import Data.Bank;
import Data.Customer;
import Data.Loan;
import Exceptions.TransferException;

import javax.security.auth.login.FailedLoginException;
import java.util.List;

/**
 * Defines the contract for the server customer services in order to provide the
 * required information to satisfy the API functionality.
 */
public interface ICustomerService {

    /**
     * Allows to create a new account.
     * @param bank Account won't be created if the bank is not the one of the server the API runs at
     * @param firstName
     * @param lastName
     * @param emailAddress
     * @param phoneNumber Format: 514.000.1111
     * @param password No complexity validation at the moment
     * @return
     */
    int openAccount(Bank bank, String firstName, String lastName, String emailAddress, String phoneNumber, String password);

    /**
     * Retrieves a customer information if he has an account.
     * Else returns null.
     * @param email
     * @return
     */
    Customer getCustomer(String email, String password)
            throws FailedLoginException;

    /**
     * Retrieves a list of loans currently registered at the bank for the current user.
     * Does not retrieve loans from other banks.
     * Will return null if account doesn't exist.
     * @param accountNumber
     * @return
     */
    List<Loan> getLoans(int accountNumber);

    /**
     * Will create a new loan for the given account number
     * granted the customer's credit line is low enough.
     * Note: the customer credit line is verified against all banks.
     * By default, when a new account is created, total amount for the loans
     * should not exceed 1.500$.
     * @param bank
     * @param accountNumber
     * @param password
     * @param loanAmount requested loan amount
     * @return
     * @throws FailedLoginException this exception is thrown in case the password is incorrect
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

    /**
     * Retrieves a Customer's account information
     * @param firstName
     * @param LastName
     * @return
     */
    Account getAccount(String firstName, String LastName);

}
