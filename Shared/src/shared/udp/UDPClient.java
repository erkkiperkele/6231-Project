package shared.udp;

import java.io.Closeable;
import java.io.IOException;
import java.net.*;

/**
 * Provides a UDP server.
 */
public class UDPClient implements Closeable {

    private DatagramSocket socket;
    private InetAddress host;
    private static final int TIME_OUT = 5000;


    public UDPClient() {

        try {
            this.host = InetAddress.getByName("localhost");
            this.socket = new DatagramSocket();
            this.socket.setSoTimeout(TIME_OUT);

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a request at the given port for a customer's credit line using a serialized GetLoanMessage()
     * (the port should correspond to the desired bank's server)
     * returns 0 if Customer has no account at the requested bank.
     * @param message a serialized GetLoanMessage() Please use the serializer provided to ensure message is valid.
     * @param serverPort the server port of the bank we want to retrieve the credit line from.
     * @return a serialized long indicating what's the customer's credit line at the requested bank.
     * @throws IOException
     */
    public byte[] sendMessage(byte[] message, int serverPort) throws IOException {
        return sendMessage(message, this.host, serverPort);
    }

    public byte[] sendMessage(byte[] message, String serverAddress, int serverPort) throws IOException {
        return sendMessage(message, InetAddress.getByName(serverAddress), serverPort);
    }

    public byte[] sendMessage(byte[] message, InetAddress serverAddress, int serverPort) throws IOException {

        DatagramPacket request = new DatagramPacket(message, message.length, serverAddress, serverPort);
        this.socket.send(request);

        System.out.println(String.format("UDP CLIENT is waiting answer on port: %d", this.socket.getLocalPort()));

        byte[] buffer = new byte[4096];
        DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

        this.socket.receive(reply);
        System.out.println(String.format("UDP CLIENT received answer!"));

        return reply.getData();
    }

    public void sendMessageAndForget(byte[] message, InetAddress serverAddress, int serverPort) throws IOException {

        DatagramPacket request = new DatagramPacket(message, message.length, serverAddress, serverPort);
        this.socket.send(request);

        System.out.println(String.format("UDP CLIENT fired and forgot."));
    }

    @Override
    public void close() throws IOException {
        if (this.socket != null) {
            this.socket.close();
        }
    }
}
