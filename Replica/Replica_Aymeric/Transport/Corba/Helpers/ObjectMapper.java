package Transport.Corba.Helpers;

import Transport.Corba.BankServerPackage.*;
import Transport.Corba.BankServerPackage.Account;
import Transport.Corba.BankServerPackage.Bank;
import Transport.Corba.BankServerPackage.Customer;
import Transport.Corba.BankServerPackage.Loan;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ObjectMapper {

    public static Transport.Corba.BankServerPackage.Bank toCorbaBank(Data.Bank bank) {

        Transport.Corba.BankServerPackage.Bank corbaBank = null;
        try {
            corbaBank = ObjectMapper.mapToCorbaObject(bank);
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
        return corbaBank;
    }

    public static Transport.Corba.BankServerPackage.Date toCorbaDate(Date date) {

        return mapToCorbaObject(date);
    }

    public static Customer toCorbaCustomer(Data.Customer customer) {
        return mapToCorbaObject(customer);
    }

    public static Loan toCorbaLoan(Data.Loan serverLoan) {
        return mapToCorbaObject(serverLoan);
    }

    public static BankInfo toCorbaBankInfo(Data.CustomerInfo[] customersInfo) {

        List<Transport.Corba.BankServerPackage.CustomerInfo> corbaCustomers = new ArrayList<>();

        for (Data.CustomerInfo customerInfo : customersInfo) {
            corbaCustomers.add(mapToCorbaObject(customerInfo));
        }

        return new BankInfo(
                corbaCustomers.stream().toArray(Transport.Corba.BankServerPackage.CustomerInfo[]::new)
        );
    }

    public static Data.Customer toCustomer(Transport.Corba.BankServerPackage.Customer corbaCustomer) {

        Data.Customer customer = null;
        try {
            customer = ObjectMapper.mapToClientObject(corbaCustomer);
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
        return customer;
    }

    public static Data.Loan toLoan(Transport.Corba.BankServerPackage.Loan corbaLoan) {

        Data.Loan loan = null;
        try {
            loan = ObjectMapper.mapToClientObject(corbaLoan);
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
        return loan;
    }

    public static Data.Bank toBank(Transport.Corba.BankServerPackage.Bank corbaBank) {

        Data.Bank bank = null;
        try {
            bank = ObjectMapper.mapToClientObject(corbaBank);
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
        return bank;
    }

    public static Date toDate(Transport.Corba.BankServerPackage.Date currentDueDate) {

        return mapToClientObject(currentDueDate);
    }

    public static Data.CustomerInfo[] toCustomersInfo(BankInfo bankInfo) {

        Data.CustomerInfo[] customersInfo = new Data.CustomerInfo[0];
        try {
            customersInfo = mapToClientObject(bankInfo);
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
        return customersInfo;
    }

    private static Bank mapToCorbaObject(Data.Bank source) throws ObjectMappingException {
        Bank destination = Bank.None;

        switch (source) {
            case Dominion:
                destination = Bank.Dominion;
                break;
            case National:
                destination = Bank.National;
                break;
            case Royal:
                destination = Bank.Royal;
                break;
            case None:
                destination = Bank.None;
                break;
            default:
                throw new ObjectMappingException(
                        "This Bank is not implemented in the Corba Interface (BankServer.idl)"
                );
        }
        return destination;
    }

    private static Customer mapToCorbaObject(Data.Customer customer) {
        return new Customer(
                (short) customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                toCorbaBank(customer.getBank()),
                (short) customer.getAccountNumber(),
                customer.getPhone(),
                customer.getPassword()
        );
    }

    private static Transport.Corba.BankServerPackage.Date mapToCorbaObject(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(cal.YEAR);
        int month = cal.get(cal.MONTH) + 1;
        int day = cal.get(cal.DAY_OF_MONTH);
        return new Transport.Corba.BankServerPackage.Date(year, month, day);
    }

    private static Transport.Corba.BankServerPackage.Loan mapToCorbaObject(Data.Loan serverLoan) {

        Transport.Corba.BankServerPackage.Loan corbaLoan = new Loan(
                (short) serverLoan.getLoanNumber(),
                (short) serverLoan.getCustomerAccountNumber(),
                (int) serverLoan.getAmount(),
                mapToCorbaObject(serverLoan.getDueDate())
        );

        return corbaLoan;
    }

    private static Transport.Corba.BankServerPackage.CustomerInfo mapToCorbaObject(Data.CustomerInfo customerInfo) {

        List<Loan> loans = new ArrayList<>();
        for (Data.Loan loan : customerInfo.getLoans()) {
            loans.add(mapToCorbaObject(loan));
        }

        return new Transport.Corba.BankServerPackage.CustomerInfo(
                customerInfo.getUserName(),
                mapToCorbaObject(customerInfo.getAccount()),
                loans.stream().toArray(Loan[]::new)
        );
    }

    private static Transport.Corba.BankServerPackage.Account mapToCorbaObject(Data.Account account) {
        return new Account(
                (short) account.getAccountNumber(),
                mapToCorbaObject(account.getOwner()),
                (short) account.getCreditLimit()
        );
    }

    private static Data.Bank mapToClientObject(Bank source) throws ObjectMappingException {
        Data.Bank destination = Data.Bank.None;


        switch (source.toString()) {
            case "Dominion":
                destination = Data.Bank.Dominion;
                break;
            case "National":
                destination = Data.Bank.National;
                break;
            case "Royal":
                destination = Data.Bank.Royal;
                break;
            case "None":
                destination = Data.Bank.None;
                break;
            default:
                throw new ObjectMappingException(
                        "This Bank is not implemented in the Corba Interface (BankServer.idl)"
                );
        }
        return destination;
    }

    private static Data.Customer mapToClientObject(Customer corbaCustomer) throws ObjectMappingException {

        return new Data.Customer(
                corbaCustomer.id,
                corbaCustomer.accountNumber,
                corbaCustomer.firstName,
                corbaCustomer.lastName,
                corbaCustomer.password,
                mapToClientObject(corbaCustomer.bank),
                corbaCustomer.email,
                corbaCustomer.phone
        );
    }

    private static Data.Loan mapToClientObject(Loan corbaLoan) throws ObjectMappingException {

        return new Data.Loan(
                corbaLoan.loanNumber,
                corbaLoan.customerAccountNumber,
                corbaLoan.amount,
                mapToClientObject(corbaLoan.dueDate)
        );
    }

    private static Date mapToClientObject(Transport.Corba.BankServerPackage.Date corbaDate) {

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.DAY_OF_MONTH, corbaDate.day);
        calendar.set(Calendar.MONTH, corbaDate.month - 1);
        calendar.set(Calendar.YEAR, corbaDate.year);
        return calendar.getTime();
    }

    private static Data.CustomerInfo[] mapToClientObject(BankInfo bankInfo) throws ObjectMappingException {
        List<Data.CustomerInfo> customersInfo = new ArrayList<>();

        Transport.Corba.BankServerPackage.CustomerInfo[] corbaCustomersInfos = bankInfo.customersInfo;
        for (Transport.Corba.BankServerPackage.CustomerInfo customerInfo : corbaCustomersInfos) {
            customersInfo.add(mapToClientObject(customerInfo));
        }

        return customersInfo.stream().toArray(Data.CustomerInfo[]::new);
    }

    private static Data.CustomerInfo mapToClientObject(Transport.Corba.BankServerPackage.CustomerInfo corbaCustomerInfo)
            throws ObjectMappingException {

        List<Data.Loan> loans = new ArrayList<>();

        for (Transport.Corba.BankServerPackage.Loan loan : corbaCustomerInfo.loans) {
            loans.add(mapToClientObject(loan));
        }

        return new Data.CustomerInfo(
                mapToClientObject(corbaCustomerInfo.account),
                loans
        );
    }

    private static Data.Account mapToClientObject(Transport.Corba.BankServerPackage.Account corbaAccount) throws ObjectMappingException {

        return new Data.Account(
                corbaAccount.accountNumber,
                mapToClientObject(corbaAccount.owner),
                corbaAccount.creditLimit
        );

    }
}
