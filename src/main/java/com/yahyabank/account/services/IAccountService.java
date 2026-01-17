package com.yahyabank.account.services;

import com.yahyabank.account.dtos.AccountDTO;
import com.yahyabank.account.entity.Account;
import com.yahyabank.auth_users.entity.User;
import com.yahyabank.enums.AccountType;
import com.yahyabank.response.Response;

import java.util.List;

public interface IAccountService {

    Account createAccount(AccountType accountType, User user);

    Response<List<AccountDTO>> getMyAccounts();

    Response<?>closeAccount(String accountNumber);





}
