package impl;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;

import net.rudp.*;

/** UDP Socket for DLMS BankServer object
 * Implementation copied from COMP 6231 example code: class MyDataGramSocket
 * 
 */
public class BankSocket extends ReliableSocket {

	static final int MAX_LEN = 100;
	DatagramPacket lastReceived;
	private ObjectInputStream recvStream;
	private ObjectOutputStream sendStream;
	
	// Constructor accepts port number
	public BankSocket(int port) throws SocketException {
		super(new DatagramSocket(port));
		try {
			recvStream = new ObjectInputStream(this.getInputStream());
			sendStream = new ObjectOutputStream(this.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public BankSocket() throws SocketException {
		super(new DatagramSocket());
		try {
			recvStream = new ObjectInputStream(this.getInputStream());
			sendStream = new ObjectOutputStream(this.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendMessage(InetAddress receiverHost, int receiverPort, String message) throws IOException {
		byte[] sendBuffer = message.getBytes();
		DatagramPacket datagram = new DatagramPacket(sendBuffer, sendBuffer.length, receiverHost, receiverPort);
		sendStream.writeObject(datagram);
	}
	
	public String recvMessage() throws IOException {
		byte[] recvBuffer = new byte[MAX_LEN];
		DatagramPacket datagram;
		try {
			datagram = (DatagramPacket)recvStream.readObject();
			this.lastReceived = datagram;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
