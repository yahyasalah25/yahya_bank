package com.yahyabank.transaction.services;

import com.yahyabank.response.Response;
import com.yahyabank.transaction.dtos.TransactionDTO;
import com.yahyabank.transaction.dtos.TransactionRequest;

import java.util.List;

public interface ITransactionService {

    Response<?> createTransaction(TransactionRequest transactionRequest);

    Response<List<TransactionDTO>> getTransactionForMyAccount(String accountNumber, int page , int size);
}
