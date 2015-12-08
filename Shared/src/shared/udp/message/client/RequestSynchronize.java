package shared.udp.message.client;

import java.io.Serializable;

import shared.udp.IOperationMessage;
import shared.udp.OperationType;

@SuppressWarnings("serial")
public class RequestSynchronize implements Serializable, IOperationMessage 
{
	private String bank;
	private String ipAddress;
	private int port;
	private String machineName;
	private boolean syncDone;

	public RequestSynchronize() {}
	public RequestSynchronize(String machineName, String bank, String ipAddress, int port)
	{
		this.syncDone = false;
		this.machineName = machineName;
		this.bank = bank;
		this.ipAddress = ipAddress;
		this.port = port;
	}

	public OperationType getOperationType()
	{
		return OperationType.RequestSynchronize;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getBank() {
		return bank;
	}

	@Override
	public String getMachineName() {
		return this.machineName;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public boolean isSyncDone() {
		return syncDone;
	}
	public void setSyncDone(boolean syncDone) {
		this.syncDone = syncDone;
	}
}
