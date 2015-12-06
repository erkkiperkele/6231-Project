import shared.data.Bank;
import shared.data.ReplicaState;
import shared.udp.Serializer;
import shared.udp.message.state.PingMessage;
import shared.udp.message.state.ReplicaManagerMessage;
import shared.udp.message.state.StateMessage;
import shared.udp.message.state.StateMessageType;
import shared.util.Constant;
import shared.util.Env;

import java.io.IOException;
import java.net.*;

/**
 * This is the UDP listener class/thread which receives the operation results
 * from the replicas and makes it available to the thread corresponding to the
 * original request from the client
 *
 * @author mat
 */

public class UdpRMtoRMListener extends Thread {

    private static IReplicaStateService replicaStateService;


    public UdpRMtoRMListener(IReplicaStateService replicaStateService) {

        this.replicaStateService = replicaStateService;
    }

    @Override
    public void run() {
        //HACK to get the serverInfos.
        Env.setCurrentBank(Bank.Royal);
        int serverPortRMtoRMListener = Env.getReplicaToReplicaManagerServerInfo().getPort();

        DatagramSocket listenerSocket = null;

        try {
            listenerSocket = new DatagramSocket(serverPortRMtoRMListener);
            startListener(listenerSocket);

        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (listenerSocket != null) {
                listenerSocket.close();
            }

        }
    }

    private void startListener(DatagramSocket messengerSocket) throws IOException {
        byte[] buffer = new byte[4096];

        while (true) {
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
            messengerSocket.receive(request);

            final DatagramSocket finalSocket = messengerSocket;
            Thread processRequest = createProcessRequestThread(finalSocket, request);
            processRequest.start();
        }
    }

    private Thread createProcessRequestThread(DatagramSocket aSocket, DatagramPacket request) {
        Thread processRequest = new Thread(() ->
        {
            try {

                System.out.println(String.format("[UDP - RM to RM] Server received Message"));

                byte[] messageBytes = request.getData();
                int answerPort = request.getPort();
                String message = new String(messageBytes, "UTF-8").trim();
                processMessage(message, request.getAddress(), answerPort, aSocket);

                System.out.println(String.format("[UDP - RM to RM] Server Answered Message on port: %d", answerPort));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        );
        return processRequest;
    }

    private void processMessage(String message, InetAddress address, int answerPort, DatagramSocket socket) {

        System.out.println(message);
        switch (message) {
            case Constant.GET_STATE:
                processStateRequest(address, answerPort, socket);
                break;
            case Constant.GET_PING:
                processPingRequest(address, answerPort, socket);
                break;
        }
    }

    private void processPingRequest(InetAddress answerAddress, int answerPort, DatagramSocket socket) {

        boolean answer = replicaStateService.isAlive();

        PingMessage pingMessage = new PingMessage(answer);
        ReplicaManagerMessage rmMessage = new ReplicaManagerMessage(StateMessageType.ping, pingMessage);

        try {
            byte[] answerMessage = Serializer.serialize(rmMessage);
            sendMessage(answerAddress, answerPort, socket, answerMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processStateRequest(InetAddress answerAddress, int answerPort, DatagramSocket socket) {

        ReplicaState state = replicaStateService.getReplicaState();

        try {
            StateMessage stateMessage = new StateMessage(state);
            ReplicaManagerMessage rmMessage = new ReplicaManagerMessage(StateMessageType.state, stateMessage);

            byte[] answer = Serializer.serialize(rmMessage);
            sendMessage(answerAddress, answerPort, socket, answer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(InetAddress answerAddress, int answerPort, DatagramSocket socket, byte[] answer) {
        DatagramPacket answerPacket = new DatagramPacket(
                answer,
                answer.length,
                answerAddress,
                answerPort
        );
        try {
            socket.send(answerPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
