package Data;

import java.io.Serializable;

public class Customer implements Serializable {

    private String phone;
    private int id;

    private String firstName;
    private String lastName;
    private Bank bank;

    private int accountNumber;
    private String password;
    private String email;

    public String getEmail() {

        return email;
    }

    public String getUserName() {

        return email.toLowerCase();
    }


    public int getAccountNumber() {

        return this.accountNumber;
    }

    public String getPassword() {

        return this.password;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public String getFirstName() {

        return this.firstName;
    }

    public String getLastName() {

        return this.lastName;
    }

    public Bank getBank() {

        return this.bank;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public Customer(int id, int accountNumber, String firstName, String lastName, String password, Bank bank, String email, String phone) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.bank = bank;
        this.password = password;
        this.email = email;
        this.phone = phone;
    }

    public Customer(String firstName, String lastName, String password, String email, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.email = email;
        this.phone = phone;
    }

    public String toString() {
        String displayInfo = "";

        displayInfo += (this.id + " - ");
        displayInfo += (this.accountNumber + " - ");
        displayInfo += (this.firstName + " - ");
        displayInfo += (this.lastName + " - ");
        displayInfo += (this.bank + " - ");
        displayInfo += (this.email);

        return displayInfo;
    }
}
