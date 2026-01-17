/**
 * CREATED BY YAHYA SALAH
 * Date :1/15/2026
 * Time :1:29 PM
 * Project Name:yahyabank
 */
package com.yahyabank.audit_dashboard.service;

import com.yahyabank.account.dtos.AccountDTO;
import com.yahyabank.account.repo.AccountRepo;
import com.yahyabank.auth_users.dtos.UserDTO;
import com.yahyabank.auth_users.entity.User;
import com.yahyabank.auth_users.repo.UserRepo;
import com.yahyabank.transaction.dtos.TransactionDTO;
import com.yahyabank.transaction.repo.TransactionRepo;
import com.yahyabank.transaction.services.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditorService implements IAuditorService {

    private final TransactionRepo transactionRepo;
    private final UserRepo userRepo;
    private final AccountRepo accountRepo;
    private final ModelMapper modelMapper;


    @Override
    public Map<String, Long> getSystemTotals() {

        long totalUsers = userRepo.count();
        long totalAccounts = accountRepo.count();
        long totalTransactions = transactionRepo.count();

        return Map.of(
                "totalUsers", totalUsers,
                "totalAccounts", totalAccounts,
                "totalTransactions", totalTransactions
        );
    }

    @Override
    public Optional<UserDTO> findUserByEmail(String email) {

        return userRepo.findByEmail(email).map(user->modelMapper.map(user, UserDTO.class));
    }

    @Override
    public Optional<AccountDTO> findAccountDetailsByAccountNumber(String accountNumber) {
        return accountRepo.findByAccountNumber(accountNumber).map(account->modelMapper.map(account, AccountDTO.class));
    }

    @Override
    public List<TransactionDTO> findTransactionsByAccountNumber(String accountNumber) {
        return transactionRepo.findByAccount_AccountNumber(accountNumber)
                .stream()
                .map(transaction->modelMapper.map(transaction, TransactionDTO.class))
                .toList();
    }

    @Override
    public Optional<TransactionDTO> findTransactionById(Long transactionId) {

        return transactionRepo.findById(transactionId).map(transaction -> modelMapper.map(transaction, TransactionDTO.class));
    }
}
