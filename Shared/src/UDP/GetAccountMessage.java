package UDP;

import java.io.Serializable;

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
}
