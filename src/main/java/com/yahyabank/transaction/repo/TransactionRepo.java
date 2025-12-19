package com.yahyabank.transaction.repo;

import com.yahyabank.account.entity.Account;
import com.yahyabank.enums.TransactionType;
import com.yahyabank.transaction.entity.Transaction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepo extends JpaRepository<Transaction, Long> {

    Page<Transaction> findByAccount_AccountNumber(String accountNumber, Pageable pageable);

    List<Transaction> findByAccount_AccountNumber(String accountNumber);

//    List<Transaction> findByAccount(Account account);




















}
