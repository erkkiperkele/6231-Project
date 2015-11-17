package Server;

import Contracts.*;
import Data.Bank;
import Data.Customer;
import Data.CustomerInfo;
import Data.Loan;
import Exceptions.RecordNotFoundException;

import javax.security.auth.login.FailedLoginException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;

public class BankServerRMI implements ICustomerRMIServer, IManagerRMIServer {

    private IBankService bankService;
    private int serverPort;

    public BankServerRMI(IBankService bankService, int serverPort) {
        this.bankService = bankService;
        this.serverPort = serverPort;
    }

    /**
     * Exports both the customer and manager RMI servers.
     * Actually both are the same server, but 2 endpoints
     * (so that customers and managers can't access each others API)
     *
     * @throws Exception
     */
    public void exportServer() throws Exception {
        Remote obj = UnicastRemoteObject.exportObject(this, serverPort);
        Registry r = LocateRegistry.createRegistry(serverPort);

        r.bind("customer", obj);    //Access for customer console
        r.bind("manager", obj);    //Access for manager console
    }

    @Override
    public int openAccount(Bank bank, String firstName, String lastName, String emailAddress, String phoneNumber, String password)
            throws RemoteException {

        int accountNumber = bankService.openAccount(bank, firstName, lastName, emailAddress, phoneNumber, password);

        return accountNumber;
    }

    @Override
    public Customer getCustomer(Bank bank, String email, String password)
            throws RemoteException, FailedLoginException {

        Customer foundCustomer = bankService.getCustomer(email, password);
        return foundCustomer;
    }

    @Override
    public Customer signIn(Bank bank, String email, String password) throws RemoteException, FailedLoginException {
            return getCustomer(bank, email, password);
    }

    @Override
    public Loan getLoan(Bank bank, int accountNumber, String password, long loanAmount)
            throws RemoteException, FailedLoginException {

        Loan newLoan = bankService.getLoan(bank, accountNumber, password, loanAmount);
        return newLoan;
    }

    @Override
    public void delayPayment(Bank bank, int loanID, Date currentDueDate, Date newDueDate) throws RecordNotFoundException {
        bankService.delayPayment(bank, loanID, currentDueDate, newDueDate);
    }

    @Override
    public CustomerInfo[] getCustomersInfo(Bank bank) throws FailedLoginException {
        return bankService.getCustomersInfo(bank);
    }
}
