import shared.data.ReplicaState;
import shared.udp.Serializer;
import shared.udp.message.state.PingMessage;
import shared.udp.message.state.ReplicaManagerMessage;
import shared.udp.message.state.StateMessage;
import shared.udp.message.state.StateMessageType;

import java.io.IOException;
import java.net.*;

/**
 * This is the UDP listener class/thread which receives the operation results
 * from the replicas and makes it available to the thread corresponding to the
 * original request from the client
 *
 * @author mat
 */

public class UdpRMtoRM {

    private static final String RM_HOST = "localhost";
    private static IReplicaStateService replicaStateService;


    public UdpRMtoRM(IReplicaStateService replicaStateService) {
        this.replicaStateService = replicaStateService;
    }


    public void startServer() {

        int serverPortRMtoRMMessenger = 6666;
        int serverPortRMtoRMListener = 6667;

        DatagramSocket messengerSocket = null;
        DatagramSocket listenerSocket = null;

        try {
            messengerSocket = new DatagramSocket(serverPortRMtoRMMessenger);
            listenerSocket = new DatagramSocket(serverPortRMtoRMListener);
            startMessenger(messengerSocket);
            startListener(listenerSocket);

        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (messengerSocket != null) {
                messengerSocket.close();
            }
            if (listenerSocket != null) {
                listenerSocket.close();
            }

        }

    }

    private void startListener(DatagramSocket socket) throws IOException {
        byte[] buffer = new byte[4096];

        while (true) {
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
            socket.receive(request);

            final DatagramSocket finalSocket = socket;
            Thread processAnswer = createProcessAnswerThread(finalSocket, request);
            processAnswer.start();

        }
    }

    private void startMessenger(DatagramSocket messengerSocket) throws IOException {
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

                System.out.println(String.format("[UDP] Server received Message"));

                byte[] messageBytes = request.getData();
                int answerPort = request.getPort();
                String message = new String(messageBytes, "UTF-8");
                processMessage(message, request.getAddress(), answerPort, aSocket);

                System.out.println(String.format("[UDP] Server Answered Message on port: %d", answerPort));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return processRequest;
    }

    private Thread createProcessAnswerThread(DatagramSocket socket, DatagramPacket packet) {
        Thread processAnswer = new Thread(() ->
        {
            try {

                System.out.println(String.format("[UDP] Server received Answer"));

                byte[] messageBytes = packet.getData();

                ReplicaManagerMessage answer = Serializer.deserialize(messageBytes);

                processAnswerMessage(answer);

                System.out.println(String.format("[UDP] Server received answer"));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        return processAnswer;
    }

    private void processAnswerMessage(ReplicaManagerMessage answer) {
        switch (answer.getStateMessageType())
        {
            case ping:
                processPingAnswer(answer);
                break;
            case state:
                processStateAnswer(answer);
        }

    }

    private void processStateAnswer(ReplicaManagerMessage answer) {
        ReplicaState state = ((StateMessage)answer.getAnswer())
                .getReplicaState();
        replicaStateService.setReplicaState(state);
    }

    private void processPingAnswer(ReplicaManagerMessage answer) {
        //TODO: ping is not used at the moment.
    }

    private void processMessage(String message, InetAddress address, int answerPort, DatagramSocket socket) {

        switch (message.toLowerCase()) {
            case "getState":
                processStateRequest(address, answerPort, socket);
                break;
            case "ping":
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
