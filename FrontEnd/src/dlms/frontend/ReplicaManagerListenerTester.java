package dlms.frontend;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.logging.Logger;

import shared.udp.Serializer;
import shared.util.Constant;

/**
 * 
 * 
 * @author mat
 *
 */
public class ReplicaManagerListenerTester {

	public static void main(String[] args) {
		
		Logger logger = Logger.getLogger("FrontEnd");
		ReplicaManagerListener rml = new ReplicaManagerListener(logger);
		rml.start();
		
		byte[] msg = new byte[4096];
		try {
			msg = Serializer.serialize(Constant.STOP_FE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		InetSocketAddress remoteAddr = new InetSocketAddress(Constant.FE_TO_RM_LISTENER_HOST, Constant.FE_TO_RM_LISTENER_PORT);
		UdpSend sender = new UdpSend(msg, remoteAddr);
		try {
			sender.call();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
