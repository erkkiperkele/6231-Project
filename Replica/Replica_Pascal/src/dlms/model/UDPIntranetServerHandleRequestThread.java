package dlms.model;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.logging.Level;

import dlms.model.udpobject.UDPRequestData;
import dlms.model.udpobject.UDPResponseTransferLoan;
import dlms.util.Constant;
import dlms.util.EnvP;
import shared.data.Customer;
import shared.data.Loan;
import shared.util.Env;

public class UDPIntranetServerHandleRequestThread implements Runnable
{
	private Thread t = null;
	private String key;
	private boolean waitForNextDatagram = true;
	private ServerBank bank;
	private DatagramPacket receivedDatagram;
	private DatagramSocket aSocket;
	private HashMap<String, UDPIntranetServerHandleRequestThread> dicHandleRequest;

	public UDPIntranetServerHandleRequestThread(String key, ServerBank bank, DatagramSocket aSocket, DatagramPacket receivedDatagram,
			HashMap<String, UDPIntranetServerHandleRequestThread> dicHandleRequest)
	{
		this.dicHandleRequest = dicHandleRequest;
		this.aSocket = aSocket;
		this.bank = bank;
		this.receivedDatagram = receivedDatagram;
		this.key = key;
		t = new Thread(this);
		t.start();
	}

	public synchronized void resumeNextDatagramReceived(DatagramPacket receivedDatagram)
	{
		this.receivedDatagram = receivedDatagram;
		waitForNextDatagram = false;
		this.notifyAll();
	}

	public synchronized void waitUntilNextDatagram()
	{
		while (waitForNextDatagram)
		{
			try
			{
				this.wait();
			}
			catch (Exception e)
			{

			}
		}
		waitForNextDatagram = true;
	}

	/**
	 * Used to terminate early in case of extreme error that shouldn't happen
	 * 
	 * @param e
	 */
	private void informErrorAndTerminate(Exception e)
	{
		Env.log(Level.FINE, key + "[UDP] Terminated with Exception " + e.getMessage());
		synchronized (dicHandleRequest)
		{
			dicHandleRequest.remove(key);
		}
	}

	@Override
	public void run()
	{
		DatagramPacket request = receivedDatagram;
		UDPRequestData messageRequested = EnvP.getObjectFromDatagram(request);

		Env.log(Level.FINE, key + "[UDP] starting.");
		if (messageRequested != null)
		{
			int accountID = messageRequested.getCustomer().getAccountNumber();
			String username = messageRequested.getUsername();
			Loan loan = this.bank.getLoans().get(username, accountID);
			boolean isCustomerExist = loan != null;

			if (messageRequested.getTypeOfRequest() == TypeOfRequest.requestLoanCheck)
			{
				try
				{
					requestLoanCheck(request, messageRequested, loan, isCustomerExist);
				}
				catch (Exception e)
				{
					informErrorAndTerminate(e);
					return;
				}
			}
			else if (messageRequested.getTypeOfRequest() == TypeOfRequest.transferLoan)
			{
				try
				{
					tranferLoan(request, messageRequested, username);
				}
				catch (Exception e)
				{
					informErrorAndTerminate(e);
					return;
				}
			}
		}

		Env.log(Level.FINE, key + "[UDP] terminated normally.");
		synchronized (dicHandleRequest)
		{
			dicHandleRequest.remove(key);
		}
	}

	/**
	 * tranferLoan method by UDP
	 * @param request
	 * @param messageRequested
	 * @param username
	 * @throws Exception
	 */
	private void tranferLoan(DatagramPacket request, UDPRequestData messageRequested, String username) throws Exception
	{
		Loan loan;
		Env.log(Level.FINE, key + "[UDP] transferLoan " + messageRequested.getUsername());
		boolean isLoanTransfered = false;
		int accountNameCreated = -1;
		Customer customer = this.bank.getAccounts().get(username);
		if (customer == null)
		{
			Env.log(Level.FINE, key + "[UDP] transferLoan Creating User Temporarely" + messageRequested.getUsername());
			try
			{
				accountNameCreated = bank.openAccount(
						messageRequested.getCustomer().getFirstName(), 
						messageRequested.getCustomer().getLastName(),
						messageRequested.getCustomer().getEmail(), 
						messageRequested.getCustomer().getPhone(), 
						messageRequested.getCustomer().getPassword());
			}
			catch (Exception e)
			{
				// handled by the parent function
				throw e;
			}

			// Creating the customer to lock the resource
			customer = this.bank.getAccounts().get(username);
		}

		if (customer != null)
		{
			synchronized (customer)
			{
				// Make sure no loan was created since creating
				// loan also lock on customer
				loan = this.bank.getLoans().get(username, customer.getAccountNumber());
				boolean isResourceLocked = customer != null && loan == null;
				Env.log(Level.FINE, key + "[UDP] transferLoan Sending locked resource " + (isResourceLocked));
				byte[] response = new byte[1];
				response[0] = (isResourceLocked) ? Constant.RESOURCE_LOCK_SUCCESSFUL : Constant.RESOURCE_LOCK_FAILED;

				if (isResourceLocked)
				{
					DatagramPacket reply = new DatagramPacket(response, response.length, request.getAddress(), request.getPort());
					try
					{
						// lock to wait for next DatagramPacket which is the answer of this reply
						waitForNextDatagram = true;
						
						aSocket.send(reply);
						
						// wait for the response before continuing
						waitUntilNextDatagram();
					}
					catch (IOException e)
					{
						// handled by the parent function
						throw e;
					}

					Env.log(Level.FINE, key + "[UDP] transferLoan Receiving loan object.");

					DatagramPacket requestTransfer = receivedDatagram;
					UDPResponseTransferLoan responseTransfer = EnvP.getObjectFromDatagram(requestTransfer);

					if (responseTransfer.transferLoanApproved())
					{
						isLoanTransfered = true;
						try
						{
							Env.log(Level.FINE, key + "[UDP] transferLoan createTransferedLoan.");
							bank.createTransferedLoan(customer, responseTransfer.getLoan());
						}
						catch (Exception e)
						{
							Env.log(Level.SEVERE, key + "Loan transfer failed: " + e.getMessage());
							isLoanTransfered = false;
						}

						byte[] responseTransferEnd = new byte[1];
						response[0] = (isLoanTransfered) ? Constant.TRANSFER_SUCCESSFUL : Constant.TRANSFER_FAILED;
						DatagramPacket replyTransfulEnd = new DatagramPacket(responseTransferEnd, responseTransferEnd.length, request.getAddress(),
								request.getPort());
						try
						{
							aSocket.send(replyTransfulEnd);
						}
						catch (IOException e)
						{
							// handled by the parent function
							throw e;
						}
					}
				}
			}
		}
		else
		{
			Env.log(Level.FINE, key + "[UDP] transferLoan Sending locking resource failed.");
			byte[] response = new byte[1];
			response[0] = Constant.RESOURCE_LOCK_FAILED;
			DatagramPacket reply = new DatagramPacket(response, response.length, request.getAddress(), request.getPort());
			try
			{
				aSocket.send(reply);
			}
			catch (IOException e)
			{
				// handled by the parent function
				throw e;
			}
		}

		if (isLoanTransfered == false && accountNameCreated > 0)
		{
			Env.log(Level.FINE, key + "[UDP] transferLoan Failed Removing User");
			this.bank.getAccounts().remove(customer.getUserName());
			try
			{
				this.bank.getAccounts().commit(EnvP.getServerCustomersFile(this.bank.getServerName(), customer.getUserName()), customer.getUserName());
			}
			catch (Exception e)
			{
				// handled by the parent function
				throw e;
			}
		}
	}

	private void requestLoanCheck(DatagramPacket request, UDPRequestData messageRequested, Loan loan, boolean isCustomerExist) throws Exception
	{
		Env.log(Level.FINE, key + "[UDP] RequestLoanCheck " + messageRequested.getUsername());
		byte[] response;
		if (isCustomerExist)
		{
			long loanAmount = loan.getAmount();
			response = new byte[Constant.SIZE_BUFFER_RESPONSE];
			response[0] = Constant.VALID_CUSTOMER;
			ByteBuffer.wrap(response).putDouble(1, loanAmount);
		}
		else
		{
			response = new byte[1];
			response[0] = Constant.ACCOUNTID_NOT_FOUND;
		}

		DatagramPacket reply = new DatagramPacket(response, response.length, request.getAddress(), request.getPort());
		try
		{
			aSocket.send(reply);
		}
		catch (IOException e)
		{
			// handled by the parent function
			throw e;
		}
	}
}
