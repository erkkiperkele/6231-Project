package impl;
import java.io.IOException;
import java.net.*;

/** UDP Socket for DLMS BankServer object
 * Implementation copied from COMP 6231 example code: class MyDataGramSocket
 * 
 */
public class BankSocket extends DatagramSocket {

	static final int MAX_LEN = 100;
	DatagramPacket lastReceived;
	
	// Constructor accepts port number
	public BankSocket(int port) throws SocketException {
		super(port);
	}
	
	public BankSocket() throws SocketException {
		super();
	}
	
	public void sendMessage(InetAddress receiverHost, int receiverPort, String message) throws IOException {
		byte[] sendBuffer = message.getBytes();
		DatagramPacket datagram = new DatagramPacket(sendBuffer, sendBuffer.length, receiverHost, receiverPort);
		this.send(datagram);
	}
	
	public String recvMessage() throws IOException {
		byte[] recvBuffer = new byte[MAX_LEN];
		DatagramPacket datagram = new DatagramPacket(recvBuffer, MAX_LEN);
		this.receive(datagram);
		this.lastReceived = datagram;
		String message = new String(recvBuffer).trim();
		return message;
	}
	
	public int getResponsePort() {
		return lastReceived.getPort();
	}
	
	public InetAddress getResponseIP() {
		return lastReceived.getAddress();
	}
	
	public void sendReply(String message) throws IOException {
		this.sendMessage(getResponseIP(), getResponsePort(), message);
	}
}
