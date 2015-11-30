package dlms.model;

import shared.udp.UDPMessage;
import shared.udp.message.*;
import shared.udp.message.client.DelayPaymentMessage;
import shared.udp.message.client.GetLoanMessage;
import shared.udp.message.client.OpenAccountMessage;
import shared.udp.message.client.PrintCustomerInfoMessage;
import shared.udp.message.client.TransferLoanMessage;

public class UDPServerHandleRequestThread extends shared.udp.UDPServerHandleRequestThread
{
	/**
	 * Method can be overwritten for FE, Sequencer, Replica, ReplicaManager
	 * @param bank
	 * @param udpMessage
	 * @throws Exception 
	 */
	protected void processRequest(UDPMessage udpMessage) throws Exception
	{
		switch (udpMessage.getOperation())
		{
			case OpenAccount:
				processOpenAccount(udpMessage);
				break;
			case GetLoan:
				processGetLoan(udpMessage);
				break;
			case DelayPayment:
				processDelayPayment(udpMessage);
				break;
			case PrintCustomerInfo:
				processPrintCustomerInfo(udpMessage);
				break;
			case TransferLoan:
				processTransferLoan(udpMessage);
				break;
			default:
				System.out.println("Unknown processRequest! " + udpMessage.getOperation().toString());
				break;
		}
	}

	/**
	 * processOpenAccount
	 * @param udpMessage is an instance of OpenAccountMessage
	 * @throws Exception
	 */
	private void processOpenAccount(UDPMessage udpMessage) throws Exception 
	{
		if(udpMessage.getMessage() instanceof OpenAccountMessage)
		{
			OpenAccountMessage msg = (OpenAccountMessage)udpMessage.getMessage();
			if(!this.bank.getServerName().equalsIgnoreCase(msg.getBank()))
			{
				msg.setException(new Exception("Wrong Bank Name! Request denied. processOpenAccount"));
			}
			else
			{
				int accountNumber = -1;
				try {
					accountNumber = bank.openAccount(
											msg.getFirstName(), 
											msg.getLastName(), 
											msg.getEmailAddress(), 
											msg.getPhoneNumber(), 
											msg.getPassword());
				} 
				catch (Exception e) 
				{
					msg.setException(e);
					accountNumber = -1;
				}
				msg.setResultAccountID(accountNumber);
			}
			this.send(udpMessage);
		}
		else
		{
			throw new Exception("Invalid Object processOpenAccount");
		}
	}

	/**
	 * processGetLoan
	 * @param udpMessage is an instance of GetLoanMessage
	 * @throws Exception
	 */
	private void processGetLoan(UDPMessage udpMessage) throws Exception 
	{
		if(udpMessage.getMessage() instanceof GetLoanMessage)
		{
			GetLoanMessage msg = (GetLoanMessage)udpMessage.getMessage();
			if(!this.bank.getServerName().equalsIgnoreCase(msg.getBank()))
			{
				msg.setException(new Exception("Wrong Bank Name! Request denied. processGetLoan"));
			}
			else
			{
				int loanNumber = -1;
				try {
					loanNumber = bank.getLoan(
							msg.getAccountNumber(),
							msg.getPassword(),
							msg.getLoanAmount());
				} catch (Exception e) {
					msg.setException(e);
				}
				msg.setResultLoanID(loanNumber);
			}
			this.send(udpMessage);
		}
		else
		{
			throw new Exception("Invalid Object processGetLoan");
		}
	}

	/**
	 * processDelayPayment
	 * @param udpMessage is an instance of DelayPaymentMessage
	 * @throws Exception
	 */
	private void processDelayPayment(UDPMessage udpMessage) throws Exception 
	{
		if(udpMessage.getMessage() instanceof DelayPaymentMessage)
		{
			DelayPaymentMessage msg = (DelayPaymentMessage)udpMessage.getMessage();
			if(!this.bank.getServerName().equalsIgnoreCase(msg.getBank()))
			{
				msg.setException(new Exception("Wrong Bank Name! Request denied. processDelayPayment"));
			}
			else
			{
				boolean isDelayed = false;
				try {
					isDelayed = bank.delayPayment(msg.getLoanID(), msg.getCurrentDueDate(), msg.getNewDueDate());
				} catch (Exception e) {
					msg.setException(e);
				}
				msg.setDelaySuccessful(isDelayed);
			}
			this.send(udpMessage);
		}
		else
		{
			throw new Exception("Invalid Object processDelayPayment");
		}
	}

	/**
	 * processPrintCustomerInfo
	 * @param udpMessage is an instance of PrintCustomerInfoMessage
	 * @throws Exception
	 */
	private void processPrintCustomerInfo(UDPMessage udpMessage) throws Exception 
	{
		if(udpMessage.getMessage() instanceof PrintCustomerInfoMessage)
		{
			PrintCustomerInfoMessage msg = (PrintCustomerInfoMessage)udpMessage.getMessage();
			if(!this.bank.getServerName().equalsIgnoreCase(msg.getBank()))
			{
				msg.setException(new Exception("Wrong Bank Name! Request denied. processPrintCustomerInfo"));
			}
			else
			{
				String result = "";
				try {
					result = bank.printCustomerInfo();
				} catch (Exception e) {
					msg.setException(e);
				}
				msg.setResult(result);
			}
			this.send(udpMessage);
		}
		else
		{
			throw new Exception("Invalid Object processPrintCustomerInfo");
		}
	}

	/**
	 * processTransferLoan
	 * @param udpMessage is an instance of TransferLoanMessage
	 * @throws Exception
	 */
	private void processTransferLoan(UDPMessage udpMessage) throws Exception 
	{
		if(udpMessage.getMessage() instanceof TransferLoanMessage)
		{
			TransferLoanMessage msg = (TransferLoanMessage)udpMessage.getMessage();
			if(!this.bank.getServerName().equalsIgnoreCase(msg.getCurrentBank()))
			{
				msg.setException(new Exception("Wrong Bank Name! Request denied. processTransferLoan"));
			}
			else
			{
				boolean isTransferred = false;
				try {
					isTransferred = bank.transferLoan(msg.getLoanID(), msg.getOtherBank());
				} catch (Exception e) {
					msg.setException(e);
				}
				msg.setTransferSuccessful(isTransferred);
			}
			this.send(udpMessage);
		}
		else
		{
			throw new Exception("Invalid Object processTransferLoan");
		}
	}
}
