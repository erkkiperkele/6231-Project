package shared.udp;

import java.io.Serializable;

@SuppressWarnings("serial")
public class GetAccountMessage implements Serializable, IOperationMessage {


    private String firstName;
    private String lastName;

    public GetAccountMessage(String firstName, String lastName) {
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
		return OperationType.GetAccount;
	}
}
