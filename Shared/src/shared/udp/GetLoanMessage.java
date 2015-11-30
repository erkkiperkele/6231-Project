package shared.udp;

import java.io.Serializable;

/**
 * A serializable message to request a customer's credit line at a bank using UDP messaging.
 */
@SuppressWarnings("serial")
public class GetLoanMessage implements Serializable, IOperationMessage {


    private String firstName;
    private String lastName;

    public GetLoanMessage(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }
    
	public OperationType getOperationType()
	{
		return OperationType.GetLoan;
	}
}


