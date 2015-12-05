package shared.udp;

import shared.data.Bank;
import shared.entities.FailureType;

import java.io.Serializable;


public class ReplicaStatusMessage implements Serializable {

    private Bank bank;
    private String address;
    private FailureType failureType;


    public ReplicaStatusMessage(Bank bank, String address, FailureType failureType){

        this.bank = bank;
        this.address = address;
        this.failureType = failureType;
    }

    public Bank getBank() {
        return bank;
    }

    public String getAddress() {
        return address;
    }

    public FailureType getFailureType() {
        return failureType;
    }
}
