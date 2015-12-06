package Contracts;

import shared.data.BankState;

public interface IBankService extends ICustomerService, IManagerService {

    BankState getCurrentState();
    void setCurrentState(BankState state);
}
