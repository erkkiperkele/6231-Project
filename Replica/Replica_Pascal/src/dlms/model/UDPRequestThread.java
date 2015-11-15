package dlms.model;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import dlms.model.udpobject.UDPRequestData;
import dlms.model.udpobject.UDPResponseTransferLoan;
import dlms.util.Constant;
import dlms.util.Env;

/**
 * @author Pascal Tozzi 27664850 UDPRequestThread allowing to do asynchrone
 *         request by UDP for credit check to all bank simultaniously
 */
public class UDPRequestThread implements Runnable
{
	private String remoteHost;
	private int remotePort;
	private String username;
	private Thread t = null;
	private String errorMessage = null;
	private boolean allowRetryOnError = false;
	private Customer customer;
	private TypeOfRequest typeOfRequest;
	private Loan loan;

	private double loanAmount = -1;
	private boolean isLoanTransfered = false;

	/**
	 * Loan Check
	 * 
	 * @param remoteHost
	 * @param remotePort
	 * @param username
	 */
	public UDPRequestThread(String remoteHost, int remotePort, String username)
	{
		this.remoteHost = remoteHost;
		this.remotePort = remotePort;
		this.username = username;
		this.typeOfRequest = TypeOfRequest.requestLoanCheck;
	}

	/**
	 * Transfer Loan
	 * 
	 * @param remoteHost
	 * @param remotePort
	 * @param username
	 * @param customer
	 * @param loan
	 */
	public UDPRequestThread(String remoteHost, int remotePort, String username, Customer customer, Loan loan)
	{
		this.remoteHost = remoteHost;
		this.remotePort = remotePort;
		this.username = username;
		this.customer = customer;
		this.loan = loan;

		if (customer != null && loan != null)
		{
			this.typeOfRequest = TypeOfRequest.transferLoan;
		}
		else
		{
			this.typeOfRequest = TypeOfRequest.requestLoanCheck;
		}
	}

	public void run()
	{
		InetAddress aHost = getInetAddress(this.remoteHost);
		if (this.isError(null))
			return;

		DatagramSocket aSocket = createDatagramSocket();
		if (this.isError(aSocket))
			return;

		if (typeOfRequest == TypeOfRequest.requestLoanCheck)
		{
			sendLoanRequest(aHost, aSocket);
			if (this.isError(aSocket))
				return;

			DatagramPacket reply = recvResponseGetCustomerLoanAmount(aSocket);
			if (this.isError(aSocket))
				return;

			this.loanAmount = getLoanAmountFromDatagramResponse(reply);
		}
		else
		{
			sendLoanRequest(aHost, aSocket);
			if (this.isError(aSocket))
				return;

			DatagramPacket reply = recvResponseGetCustomerLoanAmount(aSocket);
			if (this.isError(aSocket))
				return;

			if (getLoanResourceLockConfirmed(reply))
			{
				sendLoanToTransferRequest(aHost, aSocket);
				if (this.isError(aSocket))
					return;

				reply = recvResponseGetCustomerLoanAmount(aSocket);
				if (this.isError(aSocket))
					return;

				if (getTransferCommited(reply))
				{
					setLoanTransfered(true);
				}
			}
		}

		if (aSocket != null)
		{
			aSocket.close();
		}
	}

	private boolean getTransferCommited(DatagramPacket reply)
	{
		boolean isResourceLocked = false;
		byte[] response = reply.getData();
		if (reply.getLength() > 0 && response[0] == Constant.TRANSFER_SUCCESSFUL)
		{
			isResourceLocked = true;
		}
		return isResourceLocked;
	}

	private double getLoanAmountFromDatagramResponse(DatagramPacket reply)
	{
		double loanAmount;
		byte[] response = reply.getData();
		if (reply.getLength() > 0 && response[0] == Constant.ACCOUNTID_NOT_FOUND)
		{
			loanAmount = 0;
		}
		else if (reply.getLength() == Constant.SIZE_BUFFER_RESPONSE && response[0] == Constant.VALID_CUSTOMER)
		{
			loanAmount = ByteBuffer.wrap(response).getDouble(1);
		}
		else
		{
			allowRetryOnError = true;
			loanAmount = -1;
			errorMessage = "Invalid response";
		}
		return loanAmount;
	}

	private boolean getLoanResourceLockConfirmed(DatagramPacket reply)
	{
		boolean isResourceLocked = false;
		byte[] response = reply.getData();
		if (reply.getLength() > 0 && response[0] == Constant.RESOURCE_LOCK_SUCCESSFUL)
		{
			isResourceLocked = true;
		}
		return isResourceLocked;
	}

	private DatagramPacket recvResponseGetCustomerLoanAmount(DatagramSocket aSocket)
	{
		byte[] buffer = new byte[Constant.SIZE_BUFFER_RESPONSE];
		DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
		try
		{
			aSocket.receive(reply);
		}
		catch (IOException e)
		{
			allowRetryOnError = true;
			errorMessage = e.getMessage();
			if (aSocket != null)
			{
				aSocket.close();
			}
		}
		return reply;
	}

	private void sendLoanToTransferRequest(InetAddress aHost, DatagramSocket aSocket)
	{
		UDPResponseTransferLoan data = new UDPResponseTransferLoan(loan);

		try
		{
			byte[] message = Env.getByteFromObject(data);

			DatagramPacket request = new DatagramPacket(message, message.length, aHost, remotePort);
			aSocket.send(request);
		}
		catch (Exception e)
		{
			allowRetryOnError = true;
			errorMessage = e.getMessage();
			if (aSocket != null)
			{
				aSocket.close();
			}
		}
	}

	private void sendLoanRequest(InetAddress aHost, DatagramSocket aSocket)
	{
		UDPRequestData data = new UDPRequestData(username, typeOfRequest, customer);

		try
		{
			byte[] message = Env.getByteFromObject(data);

			DatagramPacket request = new DatagramPacket(message, message.length, aHost, remotePort);
			aSocket.send(request);
		}
		catch (Exception e)
		{
			allowRetryOnError = true;
			errorMessage = e.getMessage();
			if (aSocket != null)
			{
				aSocket.close();
			}
		}
	}

	private DatagramSocket createDatagramSocket()
	{
		DatagramSocket aSocket;
		try
		{
			aSocket = new DatagramSocket();
		}
		catch (SocketException e)
		{
			errorMessage = e.getMessage();
			aSocket = null;
		}
		return aSocket;
	}

	private InetAddress getInetAddress(String remoteHost)
	{
		InetAddress aHost;
		try
		{
			aHost = InetAddress.getByName(remoteHost);
		}
		catch (UnknownHostException e)
		{
			errorMessage = e.getMessage();
			aHost = null;
		}
		return aHost;
	}

	public void join() throws InterruptedException
	{
		if (t != null)
		{
			t.join();
		}
	}

	public void start()
	{
		t = new Thread(this);
		t.start();
	}

	/**
	 * @return the isError
	 */
	public boolean isError(DatagramSocket aSocket)
	{
		boolean isError = errorMessage != null;
		if (isError && aSocket != null)
		{
			aSocket.close();
		}
		return isError;
	}

	public String getRemoteHost()
	{
		return remoteHost;
	}

	public void setRemoteHost(String remoteHost)
	{
		this.remoteHost = remoteHost;
	}

	public int getRemotePort()
	{
		return remotePort;
	}

	public void setRemotePort(int remotePort)
	{
		this.remotePort = remotePort;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getErrorMessage()
	{
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}

	public boolean isAllowRetryOnError()
	{
		return allowRetryOnError;
	}

	public void setAllowRetryOnError(boolean allowRetryOnError)
	{
		this.allowRetryOnError = allowRetryOnError;
	}

	public Customer getCustomer()
	{
		return customer;
	}

	public void setCustomer(Customer customer)
	{
		this.customer = customer;
	}

	public TypeOfRequest getTypeOfRequest()
	{
		return typeOfRequest;
	}

	public void setTypeOfRequest(TypeOfRequest typeOfRequest)
	{
		this.typeOfRequest = typeOfRequest;
	}

	public Loan getLoan()
	{
		return loan;
	}

	public void setLoan(Loan loan)
	{
		this.loan = loan;
	}

	public double getLoanAmount()
	{
		return loanAmount;
	}

	public void setLoanAmount(double loanAmount)
	{
		this.loanAmount = loanAmount;
	}

	public boolean isLoanTransfered()
	{
		return isLoanTransfered;
	}

	public void setLoanTransfered(boolean isLoanTransfered)
	{
		this.isLoanTransfered = isLoanTransfered;
	}

}
