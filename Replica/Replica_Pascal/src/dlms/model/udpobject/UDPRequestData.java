package dlms.model.udpobject;

import java.io.Serializable;

import dlms.model.Customer;
import dlms.model.TypeOfRequest;

public class UDPRequestData implements Serializable
{
	private static final long serialVersionUID = -7264583470281087978L;

	private TypeOfRequest typeOfRequest;
	private String username;
	private Customer customer;

	/**
	 * Used for a transfer loan with locking of resource and creating the
	 * customer if it doesn't exist
	 * 
	 * @param username
	 * @param customer
	 */
	public UDPRequestData(String username, TypeOfRequest typeOfRequest, Customer customer)
	{
		if (typeOfRequest == TypeOfRequest.transferLoan)
		{
			this.setCustomer(customer);
		}
		this.setTypeOfRequest(typeOfRequest);
		this.setUsername(username);
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public TypeOfRequest getTypeOfRequest()
	{
		return typeOfRequest;
	}

	public void setTypeOfRequest(TypeOfRequest typeOfRequest)
	{
		this.typeOfRequest = typeOfRequest;
	}

	public Customer getCustomer()
	{
		return customer;
	}

	public void setCustomer(Customer customer)
	{
		this.customer = customer;
	}
}
