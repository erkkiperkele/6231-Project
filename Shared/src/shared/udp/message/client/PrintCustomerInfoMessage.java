package shared.udp.message.client;

import java.io.Serializable;

import shared.udp.IOperationMessage;
import shared.udp.IOperationResult;
import shared.udp.OperationType;

@SuppressWarnings("serial")
public class PrintCustomerInfoMessage implements Serializable, IOperationMessage, IOperationResult<String>
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

	/**
	 * Used to compare this object's result with another, for use in the front
	 * end so that it doesn't have to worry about types
	 * 
	 * @param msg
	 * @return
	 */
	public boolean isResultEqual(IOperationResult<String> altMessage) {

		if (this.result == null || altMessage == null) {
			return false;
		}
		return this.result.equals(altMessage.getResult());
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
	@Override
	public String getMachineName() {
		return machineName;
	}
}
