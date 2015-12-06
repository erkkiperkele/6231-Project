package shared.udp.message.client;

import java.io.Serializable;

import shared.udp.IOperationMessage;
import shared.udp.OperationType;

@SuppressWarnings("serial")
public class PrintCustomerInfoMessage implements Serializable, IOperationMessage 
{
	private String bank;
	private String result;
	private Exception exception;
	private String machineName;

	public PrintCustomerInfoMessage() {}
	public PrintCustomerInfoMessage(String bank) 
	{
		this.bank = bank;
	}

	public OperationType getOperationType()
	{
		return OperationType.PrintCustomerInfo;
	}

	public String getBank() {
		return bank;
	}
	public void setBank(String bank) {
		this.bank = bank;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public Exception getException() {
		return exception;
	}
	public void setException(Exception exception) {
		this.exception = exception;
	}

	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}
}
