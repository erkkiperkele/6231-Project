package shared.udp;

import java.io.Serializable;

import shared.data.Customer;

@SuppressWarnings("serial")
public class CreateAccountMessage implements Serializable, IOperationMessage{


    private Customer customer;

    public Customer getCustomer() {
        return customer;
    }

    public CreateAccountMessage(Customer customer) {

        this.customer = customer;
    }
    
	public OperationType getOperationType()
	{
		return OperationType.CreateAccount;
	}
}
