
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import shared.contracts.IReplicaManagerService;
import shared.udp.ReplicaStatusMessage;
import shared.udp.Serializer;

/**
 * This is the UDP listener class/thread which receives the operation results
 * from the replicas and makes it available to the thread corresponding to the
 * original request from the client
 *
 * @author mat
 *
 */

public class UdpRMtoFEListener extends Thread {

    private static final String RM_HOST = "localhost";
    private static final int RM_PORT = 6667;
    private static final int UDP_PACKET_SIZE = 4096;
    private static IReplicaManagerService replicaManagerService;


    public UdpRMtoFEListener(IReplicaManagerService replicaManagerService) {
        this.replicaManagerService = replicaManagerService;
    }

    @Override
    public void run() {

        DatagramSocket serverSocket = null;
        InetSocketAddress localAddr = new InetSocketAddress(RM_HOST, RM_PORT);

        try {

            serverSocket = new DatagramSocket(localAddr);

            while (true) {
                ReplicaStatusMessage message = receiveMessage(serverSocket);
                processMessage(message);
            }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(serverSocket != null) serverSocket.close();
        }
    }

    private ReplicaStatusMessage receiveMessage(DatagramSocket serverSocket) throws IOException {
        byte[] receiveData;
        receiveData = new byte[UDP_PACKET_SIZE];
        final DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

        // Wait for the packet
        serverSocket.receive(receivePacket);

        byte[] data = new byte[receivePacket.getLength()];
        System.arraycopy(receivePacket.getData(), receivePacket.getOffset(), data, 0, receivePacket.getLength());


        ReplicaStatusMessage message = null;
        try {
            message = Serializer.deserialize(data);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return message;
    }

    private void processMessage(ReplicaStatusMessage message ) {

        switch (message.getFailureType()){
            case error:
                replicaManagerService.onError(message.getBank(), message.getAddress());
                break;
            case failure:
                replicaManagerService.onFailure(message.getBank(), message.getAddress());
                break;
        }
    }
}
