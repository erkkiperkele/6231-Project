package Tests.BankService;

import Contracts.IBankService;
import Data.Bank;
import Data.Customer;
import Data.CustomerInfo;
import Data.Loan;
import Exceptions.RecordNotFoundException;
import Exceptions.TransferException;
import Services.BankService;
import Services.SessionService;

import javax.security.auth.login.FailedLoginException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BankServiceTest {


    private static IBankService bankService;
    private static Bank bank;
    private static Bank anotherBank;

    public static void main(String[] args) throws FailedLoginException {

        String serverArg = args.length == 0
                ? "1"
                : args[0];

        initialize(serverArg);

        //Make sure servers are running before running tests.
        runTests();
    }

    private static void initialize(String arg) {
        bank = Bank.fromInt(Integer.parseInt(arg));
        anotherBank = bank == Bank.Royal
                ? Bank.National
                : Bank.Royal;

        SessionService.getInstance().setBank(bank);
        bankService = new BankService();
    }

    private static void runTests() throws FailedLoginException {
        testInitial();
        testOpeningMultipleAccounts();
        testUDPGetLoan();
        testPrintCustomersInfo();
        testDelayPayment();
        testTransferMultipleLoans();
    }

    public static void testInitial() {

        try {
            String unknownUsername = "dummy@dummy.com";
            Customer unknown = bankService.getCustomer(unknownUsername, "aa");
            printCustomer(unknown, unknownUsername);

            String mariaUsername = "maria.etinger@gmail.com";
            Customer maria = bankService.getCustomer(mariaUsername, "aa");
            printCustomer(maria, mariaUsername);

            String justinUsername = "justin.paquette@gmail.com";
            Customer justin = bankService.getCustomer(justinUsername, "aa");
            printCustomer(justin, justinUsername);

            String alexUserName = "alex.emond@gmail.com";
            Customer alex = null;
            alex = bankService.getCustomer(alexUserName, "aa");
            printCustomer(alex, alexUserName);

            List<Loan> alexLoans = bankService.getLoans(alex.getAccountNumber());
            printLoans(alexLoans, alex.getFirstName());

        } catch (FailedLoginException e) {
            e.printStackTrace();
        }
    }

    public static void testOpeningMultipleAccounts() {
        System.out.println(String.format("Start of concurrent account creation"));

        Bank bank = SessionService.getInstance().getBank();

        String firstName = "Concurrent";
        String lastName = "concu";
        String email = "concurrent@thread.com";
        String phone = "";
        String password = "c";

        Thread openAccountTask1 = new Thread(() ->
        {
            bankService.openAccount(bank, firstName + "1", lastName, email + "1", phone, password);
            System.out.println(String.format("thread #%d OPENED an account for %s1", Thread.currentThread().getId(), firstName));
        });

        Thread openAccountTask2 = new Thread(() ->
        {
            bankService.openAccount(bank, firstName + "2", lastName, email + "2", phone, password);
            System.out.println(String.format("thread #%d OPENED an account for %s2", Thread.currentThread().getId(), firstName));
        });

        Thread openAccountTask3 = new Thread(() ->
        {
            bankService.openAccount(bank, firstName + "3", lastName, email + "3", phone, password);
            System.out.println(String.format("thread #%d OPENED an account for %s3", Thread.currentThread().getId(), firstName));
        });

        Thread openAccountTask4 = new Thread(() ->
        {
            bankService.openAccount(bank, firstName + "4", lastName, email + "4", phone, password);
            System.out.println(String.format("thread #%d OPENED an account for %s4", Thread.currentThread().getId(), firstName));
        });

        Thread openAccountTask5 = new Thread(() ->
        {
            bankService.openAccount(bank, firstName + "5", lastName, email + "5", phone, password);
            System.out.println(String.format("thread #%d OPENED an account for %s5", Thread.currentThread().getId(), firstName));
        });

        Thread openAccountTask6 = new Thread(() ->
        {
            bankService.openAccount(bank, firstName + "6", lastName, email + "6", phone, password);
            System.out.println(String.format("thread #%d OPENED an account for %s6", Thread.currentThread().getId(), firstName));
        });

        Thread openAccountTask7 = new Thread(() ->
        {
            bankService.openAccount(bank, firstName + "7", lastName, email + "7", phone, password);
            System.out.println(String.format("thread #%d OPENED an account for %s7", Thread.currentThread().getId(), firstName));
        });

        Thread openAccountTask8 = new Thread(() ->
        {
            bankService.openAccount(bank, firstName + "8", lastName, email + "8", phone, password);
            System.out.println(String.format("thread #%d OPENED an account for %s8", Thread.currentThread().getId(), firstName));
        });

        Thread openAccountCreatedByTask1 = new Thread(() ->
        {
            System.out.println(String.format("thread #%d STARTING an account for %s1", Thread.currentThread().getId(), firstName));
            bankService.openAccount(bank, firstName + "1", lastName, email + "1", phone, password);
            System.out.println(String.format("thread #%d OPENED an account for %s1", Thread.currentThread().getId(), firstName));
        });

//        Thread getAccount = new Thread(() ->
//        {
//            bankService.getCustomer(email + "1");
//            System.out.println(String.format("thread #%d OPENED an account for %s1", Thread.currentThread().getId(), firstName));
//        });

        openAccountTask1.start();
        openAccountTask2.start();
        openAccountTask3.start();
        openAccountTask4.start();
        openAccountTask5.start();
        openAccountTask6.start();
        openAccountTask7.start();
        openAccountTask8.start();
        openAccountCreatedByTask1.start();

        System.out.println(String.format("End of concurrent account creation"));
    }

    public static void testUDPGetLoan() {

        Bank bank = SessionService.getInstance().getBank();

        if (bank == Bank.Royal) {

            int accountNumber = 2;
            String password = "aa";
            long loanAmount = 200;


            //Wait for all servers to start before sending a message.
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                bankService.getLoan(bank, accountNumber, password, loanAmount);
            } catch (FailedLoginException e) {
                e.printStackTrace();
            }
        }
    }

    public static void testPrintCustomersInfo() {
        Bank bank = SessionService.getInstance().getBank();
        try {
            CustomerInfo[] customersInfo = bankService.getCustomersInfo(bank);
            for (CustomerInfo info : customersInfo) {
                System.out.println(info.toString());
            }
        } catch (FailedLoginException e) {
            e.printStackTrace();
        }
    }

    public static void testDelayPayment() {
        Bank bank = SessionService.getInstance().getBank();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        try {
            Date currentDate = dateFormat.parse("02-03-2016");
            Date newDueDate = dateFormat.parse("18-12-2016");
            bankService.delayPayment(bank, 2, currentDate, newDueDate);

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (RecordNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void printCustomer(Customer customer, String username) {
        if (customer != null) {
            SessionService.getInstance().log().info(customer.toString());
        } else {
            SessionService.getInstance().log().info(String.format("No customer found for this username %s", username));
        }
    }

    private static void printLoans(List<Loan> loans, String customerName) {
        if (loans != null && loans.size() > 0) {
            for (Loan loan : loans) {
                SessionService.getInstance().log().info(loan.toString());
            }
        } else {
            SessionService.getInstance().log().info(String.format("%1$s has no loans currently", customerName));
        }
    }

    /**
     * Simulates 2 users each concurrently transferring 4 loans.
     * 2 of which from a first bank and the 2 others from another bank
     * note that requests are crossed (transfers both ways from both bank concurrently to make
     * sure UDP concurrencuy is properly handled.
     * @throws FailedLoginException
     */
    private static void testTransferMultipleLoans() throws FailedLoginException {


        //4 loans transferred concurrently for a user
        Loan loan1 = bankService.getLoan(bank, 2, "aa", 1);
        Loan loan2 = bankService.getLoan(bank, 2, "aa", 1);
        Loan loan3 = bankService.getLoan(anotherBank, 2, "aa", 1);
        Loan loan4 = bankService.getLoan(anotherBank, 2, "aa", 1);

        //4 loans transferred concurrently for another user
        Loan loan5 = bankService.getLoan(bank, 3, "aa", 1);
        Loan loan6 = bankService.getLoan(bank, 3, "aa", 1);
        Loan loan7 = bankService.getLoan(anotherBank, 3, "aa", 1);
        Loan loan8 = bankService.getLoan(anotherBank, 3, "aa", 1);

        Thread transferLoan1 = generateThreadTransferLoan(anotherBank, loan1);
        Thread transferLoan2 = generateThreadTransferLoan(anotherBank, loan2);
        Thread transferLoan3 = generateThreadTransferLoan(bank, loan3);
        Thread transferLoan4 = generateThreadTransferLoan(bank, loan4);

        Thread transferLoan5 = generateThreadTransferLoan(anotherBank, loan5);
        Thread transferLoan6 = generateThreadTransferLoan(anotherBank, loan6);
        Thread transferLoan7 = generateThreadTransferLoan(bank, loan7);
        Thread transferLoan8 = generateThreadTransferLoan(bank, loan8);

        transferLoan1.start();
        transferLoan2.start();
        transferLoan3.start();
        transferLoan4.start();

        transferLoan5.start();
        transferLoan6.start();
        transferLoan7.start();
        transferLoan8.start();
    }

    private static Thread generateThreadTransferLoan(Bank otherBank, Loan loan) {
        return new Thread(() ->
        {
            int loanId = loan.getLoanNumber();
            try {
                bankService.transferLoan(loanId, bank, otherBank);
                System.out.println(
                        String.format(
                                "thread #%1$d transferred loan #%2$d to bank: %3$s",
                                Thread.currentThread().getId(),
                                loanId,
                                otherBank.name())
                );
            } catch (TransferException e) {
                e.printStackTrace();
            }
        });
    }
}
