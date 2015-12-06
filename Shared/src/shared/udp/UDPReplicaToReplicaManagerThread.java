package shared.udp;

import java.net.DatagramSocket;

import shared.data.AbstractServerBank;
import shared.data.ServerInfo;
import shared.util.Env;

public class UDPReplicaToReplicaManagerThread implements Runnable
{
	private Thread t;
	private AbstractServerBank bank;
	private DatagramSocket aSocket;
	
	public UDPReplicaToReplicaManagerThread(AbstractServerBank bank) throws Exception
	{
		t = new Thread();
		ServerInfo sv = Env.getReplicaToReplicaManagerServerInfo();
		System.out.println("Binding to port " + sv.getPort() + " for " + sv.getIpAddress());
		aSocket = new DatagramSocket(sv.getPort());
	}

	@Override
	public void run() {
		
	}

	public void start() {
		t.start();
	}

	public void join() throws InterruptedException {
		t.join();
	}
}
