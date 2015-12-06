package shared.udp.message.client;

import java.io.Serializable;

import shared.data.Customer;
import shared.udp.IOperationMessage;
import shared.udp.OperationType;

@SuppressWarnings("serial")
public class SynchronizeCustomer implements Serializable, IOperationMessage 
{
	private String bank;
	private Customer customer;
	private int posCustomer;
	private int amountCustomer;
	private boolean isRequested;
	private int nextCustomerID;
	private int nextSequenceID;
	private String machineName;

	public SynchronizeCustomer() {}
	public SynchronizeCustomer(String machineName, String bank, Customer customer, int posCustomer,
			int amountCustomer, boolean isRequested, int nextCustomerID, int nextSequenceID) 
	{
		this.machineName = machineName;
		this.bank = bank;
		this.customer = customer;
		this.posCustomer = posCustomer;
		this.amountCustomer = amountCustomer;
		this.isRequested = isRequested;
		this.setNextCustomerID(nextCustomerID);
		this.setNextSequenceID(nextSequenceID);
	}

	public OperationType getOperationType()
	{
		return OperationType.SynchronizeCustomer;
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
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public int getPosCustomer() {
		return posCustomer;
	}
	public void setPosCustomer(int posCustomer) {
		this.posCustomer = posCustomer;
	}
	public int getAmountCustomer() {
		return amountCustomer;
	}
	public void setAmountCustomer(int amountCustomer) {
		this.amountCustomer = amountCustomer;
	}
	public boolean isRequested() {
		return isRequested;
	}
	public void setRequested(boolean isRequested) {
		this.isRequested = isRequested;
	}
	public int getNextCustomerID() {
		return nextCustomerID;
	}
	public void setNextCustomerID(int nextCustomerID) {
		this.nextCustomerID = nextCustomerID;
	}
	public int getNextSequenceID() {
		return nextSequenceID;
	}
	public void setNextSequenceID(int nextSequenceID) {
		this.nextSequenceID = nextSequenceID;
	}
}
