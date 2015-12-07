package Server;

import Contracts.ICustomerService;
import Services.BankService;
import Services.SessionService;
import shared.data.ServerInfo;
import shared.udp.*;
import shared.util.Env;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.security.auth.login.FailedLoginException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.List;

public class UDPServer {
    private ICustomerService customerService;

    public UDPServer(ICustomerService customerService) {

        this.customerService = customerService;
    }

    public void startServer() {

        ServerInfo serverInfo = Env.getReplicaServerInfo();
        DatagramSocket aSocket = null;

        try {
            //Setup the socket
            aSocket = new DatagramSocket(serverInfo.getAddress());
            byte[] buffer = new byte[4096];

            System.out.println("Replica listening on UDP port: " + aSocket.getPort());

            //Setup the loop to process request
            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);

                final DatagramSocket finalSocket = aSocket;
                Thread processRequest = createProcessRequestThread(finalSocket, request);
                processRequest.start();

            }
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (aSocket != null) {
                aSocket.close();
            }
        }

    }

    private Thread createProcessRequestThread(DatagramSocket aSocket, DatagramPacket request) {
        Thread processRequest = new Thread(() ->
        {
            try {

                System.out.println(String.format("[UDP] Server received Message"));

                byte[] message = request.getData();
                byte[] answer = processMessage(message);
                int answerPort = request.getPort();

                DatagramPacket reply = new DatagramPacket(
                        answer,
                        answer.length,
                        request.getAddress(),
                        answerPort
                );
                aSocket.send(reply);
                System.out.println(String.format("[UDP] Server Answered Message on port: %d", answerPort));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (FailedLoginException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return processRequest;
    }

    private byte[] processMessage(byte[] message) throws IOException, ClassNotFoundException, FailedLoginException {

        Serializer udpMessageSerializer = new Serializer<UDPMessage>();
        UDPMessage udpMessage = (UDPMessage) udpMessageSerializer.deserialize(message);

        switch (udpMessage.getOperation()) {
            case CreateAccount:
                return processCreateAccountMessage(udpMessage.getMessage());
            case GetAccount:
                return processGetAccountMessage(udpMessage.getMessage());
            case CreateLoan:
                return processCreateLoanMessage(udpMessage.getMessage());
            case GetLoan:
                return processGetLoanMessage(udpMessage.getMessage());
            default:
                throw new NotImplementedException();
        }
    }

    /**
     * Upon reception of a GetLoanMessage() the udp server will calculate the current
     * Credit line of the mentioned customer and return it to the sender of the request.
     * returns 0 if Customer has no account at the requested bank.
     *
     * @param message a serialized GetLoanMessage() Please use the serializer provided to ensure message is valid.
     * @return a serialized long indicating the current credit line at this bank for the given customer.
     * @throws IOException
     */
    private byte[] processGetLoanMessage(IOperationMessage message) throws IOException {

        long currentLoanAmount = 0;

        GetLoanMessage loanMessage = (GetLoanMessage) message;

        SessionService.getInstance().log().info(
                String.format("[UDP] Received a UDP request to get %1$s's credit line", loanMessage.getFirstName())
        );

        shared.data.Account account = this.customerService.getAccount(loanMessage.getFirstName(), loanMessage.getLastName());

        if (account != null) {
            List<shared.data.Loan> loans = this.customerService.getLoans(account.getAccountNumber());

            currentLoanAmount = loans
                    .stream()
                    .mapToLong(l -> l.getAmount())
                    .sum();

            System.out.println(String.format("Loan amount %d", currentLoanAmount));
        } else {
            SessionService.getInstance().log().info(
                    String.format("%1$s %2$s doesn't have a credit record at our bank",
                            loanMessage.getFirstName(),
                            loanMessage.getLastName())
            );
        }

        Serializer loanSerializer = new Serializer<Long>();
        byte[] serializedLoan = loanSerializer.serialize(currentLoanAmount);
        return serializedLoan;
    }

    /**
     * Note this calls a restricted operation in order to get a loan without checking the credit line
     * This is only accepted in case of a transfer where a loan is already existing.
     *
     * @param message
     * @return
     * @throws FailedLoginException
     * @throws IOException
     */
    private byte[] processCreateLoanMessage(IOperationMessage message) throws FailedLoginException, IOException {

        CreateLoanMessage loanMessage = (CreateLoanMessage) message;

        SessionService.getInstance().log().info(
                String.format("[UDP] Received a UDP request for %1$s to obtain a new loan of %2$d$",
                        loanMessage.getAccount().getOwner().getFirstName(),
                        loanMessage.getLoan().getAmount())
        );


        //REFACTOR: Should not cast interface to its implementation to access restricted operation
        shared.data.Loan loan = ((BankService) this.customerService).getLoanWithNoCreditLineCheck(
                SessionService.getInstance().getBank(),
                loanMessage.getAccount().getAccountNumber(),
                loanMessage.getAccount().getOwner().getPassword(),
                loanMessage.getLoan().getAmount()
        );

        String logMessage = String.format(
                "[UDP]: %1$s's Loan #%2$d (%3$d$) successfully TRANSFERRED as new loan #%4$d",
                loanMessage.getAccount().getOwner().getFirstName(),
                loanMessage.getLoan().getLoanNumber(),
                loan.getAmount(),
                loan.getLoanNumber()
        );

        SessionService.getInstance().log().info(logMessage);

        Serializer loanSerializer = new Serializer<shared.data.Loan>();
        byte[] serializedLoan = loanSerializer.serialize(loan);
        return serializedLoan;
    }

    private byte[] processGetAccountMessage(IOperationMessage message) throws IOException {

        GetAccountMessage accountMessage = (GetAccountMessage) message;

        SessionService.getInstance().log().info(
                String.format("[UDP] Received a UDP request to retrieve %1$s's account",
                        accountMessage.getFirstName())
        );

        shared.data.Account account = this.customerService.getAccount(
                accountMessage.getFirstName(),
                accountMessage.getLastName()
        );

        Serializer accountSerializer = new Serializer<shared.data.Account>();
        byte[] serializedAccount = accountSerializer.serialize(account);
        return serializedAccount;
    }

    private byte[] processCreateAccountMessage(IOperationMessage message) throws IOException {

        CreateAccountMessage accountMessage = (CreateAccountMessage) message;

        SessionService.getInstance().log().info(
                String.format("[UDP] Received a UDP request to create an account for %1$s",
                        accountMessage.getCustomer().getFirstName())
        );

        int accountId = this.customerService.openAccount(
                SessionService.getInstance().getBank(),
                accountMessage.getCustomer().getFirstName(),
                accountMessage.getCustomer().getLastName(),
                accountMessage.getCustomer().getEmail(),
                accountMessage.getCustomer().getPhone(),
                accountMessage.getCustomer().getPassword()
        );

        shared.data.Account account = this.customerService.getAccount(
                accountMessage.getCustomer().getFirstName(),
                accountMessage.getCustomer().getLastName()
        );

        Serializer accountSerializer = new Serializer<shared.data.Account>();
        byte[] serializedAccount = accountSerializer.serialize(account);
        return serializedAccount;
    }
}
