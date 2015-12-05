package dlms.frontend;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

import shared.udp.Serializer;
import shared.udp.UDPMessage;

public class BackendSim {
	
	private static final int BUFFER_SIZE = 4096;
	private ProcessStub sequencerStub;
	private long opSequenceNbr = 1;
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String args[]) {

		new BackendSim();
	}

	
	
	
	
	public BackendSim() {
		
		super();
		sequencerStub = new ProcessStub("sequencer", new InetSocketAddress("localhost", 5000));
		sequencerStub = new ProcessStub("frontend", new InetSocketAddress("localhost", 15000));
		runs();
	}
	
	
	
	
	public void simSendReplicaResponses(String BankId) {
		
//		PrintCustomerInfoMessage msg = new PrintCustomerInfoMessage();
//		UDPMessage r1Msg = new UDPMessage();
		
		
	}
	
	
	public void runs() {

		DatagramSocket serverSocket = null;

		try {

			serverSocket = new DatagramSocket(sequencerStub.addr);
			byte[] receiveData = new byte[BUFFER_SIZE];
			byte[] sendData = new byte[BUFFER_SIZE];

			System.out.println("TestBackend: Waiting for FE UDP Message on " + sequencerStub.addr.getHostName() + ":" + sequencerStub.addr.getPort());
			
			while (true) {

				//
				// LISTENER
				//
				
				receiveData = new byte[BUFFER_SIZE];
				final DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

				// Wait for the packet
				serverSocket.receive(receivePacket);

				System.out.println("TestBackend: Received operation message fron FE ");
				
				// Received a request. Parse it.
				byte[] data = new byte[receivePacket.getLength()];
		        System.arraycopy(receivePacket.getData(), receivePacket.getOffset(), data, 0, receivePacket.getLength());
				final InetAddress remoteIpAddress = receivePacket.getAddress();
				final int remotePort = receivePacket.getPort();

				// Received a request. Place it in the receiving data variable and parse it
				// byte[] data = new byte[receivePacket.getLength()];
		        System.arraycopy(receivePacket.getData(), receivePacket.getOffset(), data, 0, receivePacket.getLength());

		        UDPMessage message = null;
				try {
					message = (UDPMessage) Serializer.deserialize(data);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				
				// Respond to the front end with the sequence number
				
				long seqNbr = this.opSequenceNbr++;
				sendData = Serializer.serialize(seqNbr);
				final DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, remoteIpAddress, remotePort);
				
				try {
					serverSocket.send(sendPacket);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		} catch (final SocketException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		} finally {if(serverSocket != null) serverSocket.close();}
		
		
		
		
		
		
		
	}
}
