package shared.udp.console;

import shared.data.Bank;
import shared.entities.FailureType;
import shared.udp.ReplicaStatusMessage;
import shared.udp.Serializer;
import shared.util.Constant;
import sun.net.spi.nameservice.NameService;

import java.io.IOException;
import java.net.*;

public class UdpConsole {

    private static Console console;

    public static void main(String[] args) {

        console = new Console(System.in);

        boolean isExiting = false;

        while (!isExiting) {
            displayChoices();
            isExiting = executeChoice();
        }

//        String url = "localhost";
//        ReplicaStatusMessage failureMessage = new ReplicaStatusMessage(Bank.Royal, url , FailureType.failure);
//        ReplicaStatusMessage errorMessage = new ReplicaStatusMessage(Bank.Royal, url , FailureType.error);


    }

    private static void displayChoices() {

        String message = String.format(
                "Please chose an option:"
                        + "%1$s 1: send error message from FE to RM"
                        + "%1$s 2: send failure message from FE to RM"
                        + "%1$s 3: send getState message from RM to RM"
                        + "%1$s Press any other key to exit."
                , "\n");

        System.out.println(message);
    }

    private static boolean executeChoice() {

        char choice = console.readChar();
        boolean isExiting = false;

        switch (choice) {
            case '1':
                sendFrontEndToRMMessage(FailureType.error);
                break;
            case '2':
                sendFrontEndToRMMessage(FailureType.failure);
                break;
            case '3':
                sendGetStateMessage();
                break;
            default:
                console.println("See you!");
                isExiting = true;
                break;
        }
        return isExiting;
    }

    private static void sendGetStateMessage() {
        console.println("NOT YET IMPLEMENTED");
    }

    //Simulates front end sending failure message to RM on dedicated port.
    private static void sendFrontEndToRMMessage(FailureType type) {

        try {
            String url = "localhost";
            ReplicaStatusMessage message = new ReplicaStatusMessage(Bank.Royal, url, type);
            byte[] data = Serializer.serialize(message);

            DatagramSocket socket = null;
            InetAddress inetAddress = null;
            int port = Constant.RM_TO_FE_LISTENER_PORT;

            inetAddress = InetAddress.getByName(url);

            sendMessage(inetAddress, port, socket, data);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

//    private static void sendErrorMessage() {
//
//        String url = "localhost";
//        ReplicaStatusMessage message = new ReplicaStatusMessage(Bank.Royal, url , FailureType.error);
//        DatagramSocket socket = null;
//        InetAddress inetAddress = null;
//        int port = Constant.RM_TO_FE_LISTENER_PORT;
//
//
//        try {
//            inetAddress = InetAddress.getByName(url);
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
//
//        sendMessage(inetAddress, port, socket);
//
//    }


    private static void sendMessage(InetAddress address, int answerPort, DatagramSocket socket, byte[] message) {

        try {
            socket = new DatagramSocket();
            DatagramPacket answerPacket = new DatagramPacket(
                    message,
                    message.length,
                    address,
                    answerPort);
            socket.send(answerPacket);


        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }
}
