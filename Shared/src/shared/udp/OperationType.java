package shared.udp;

public enum OperationType {
    GetAccount,
    CreateAccount,
    CreateLoan,
    
    // Client messages
    OpenAccount,
    GetLoan,
    DelayPayment,
    PrintCustomerInfo,
    TransferLoan, 
    
    // Bank messages
    GetLoanAmount, 

    SynchronizeCustomer,
    SynchronizeLoan,
    RequestSynchronize
}
