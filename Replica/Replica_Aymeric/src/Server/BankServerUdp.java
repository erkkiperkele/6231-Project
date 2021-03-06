package Server;

import Contracts.IBankService;
import Services.SessionService;
import shared.data.*;
import shared.udp.UdpReplicaServiceThread;
import shared.util.Env;

import javax.security.auth.login.FailedLoginException;
import java.net.SocketException;
import java.util.Date;

/**
 * Created by Aymeric on 2015-12-01.
 */
public class BankServerUdp extends AbstractServerBank {

    private Bank bank;
    private UdpReplicaServiceThread udpServer = null;
    private IBankService bankService;

    public BankServerUdp(IBankService bankService) {


        this.bank = SessionService.getInstance().getBank();
        this.bankService = bankService;
        Env.setCurrentBank(this.bank);

        ServerInfo serverInfo = Env.getReplicaServerInfo();

        try {
            System.out.println(serverInfo.getPort() + " UDPServerThread");
            udpServer = new UdpReplicaServiceThread("Replica Aymeric", serverInfo.getPort(), this);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void start(){
        udpServer.start();
    }

    @Override
    public String getServerName() {
        return bank.name();
    }

    @Override
    public int openAccount(String firstName, String lastName, String emailAddress, String phoneNumber, String password) throws Exception {
        return bankService.openAccount(this.bank,firstName,lastName,emailAddress, phoneNumber,password);
    }

    @Override
    public int getLoan(int accountNumber, String password, long loanAmount) throws Exception {
        return bankService.getLoan(this.bank,accountNumber, password, loanAmount).getLoanNumber();
    }

    @Override
    public boolean delayPayment(int loanID, Date currentDueDate, Date newDueDate) throws Exception {
        bankService.delayPayment(this.bank, loanID, currentDueDate, newDueDate);
        return true;
    }

    @Override
    public String printCustomerInfo() {

        String result = "";
        try {
            CustomerInfo[] infos = this.bankService.getCustomersInfo(this.bank);

            if (infos == null)
            {
                return "This bank has no customers.";
            }

            for(CustomerInfo info : infos)
            {
                result += "\n" + info.toString();
            }

        } catch (FailedLoginException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean transferLoan(int loanID, String otherBank) throws Exception {
        Bank other = Bank.fromString(otherBank);
        Loan newLoan = bankService.transferLoan(loanID, this.bank, other);
        return newLoan != null;
    }

    @Override
    public BankState getCurrentState() {
        return bankService.getCurrentState();
    }

    @Override
    public void setCurrentState(BankState state) {
        bankService.setCurrentState(state);
    }
}
