package UDP;

import Data.Customer;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CreateAccountMessage implements Serializable, IOperationMessage{


    private Customer customer;

    public Customer getCustomer() {
        return customer;
    }

    public CreateAccountMessage(Customer customer) {

        this.customer = customer;
    }
}