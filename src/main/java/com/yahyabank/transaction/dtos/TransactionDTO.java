package com.yahyabank.transaction.dtos;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.yahyabank.account.dtos.AccountDTO;
import com.yahyabank.account.entity.Account;
import com.yahyabank.enums.TransactionStatus;
import com.yahyabank.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionDTO {

    private String id;


    private BigDecimal amount;


    private TransactionType type;

    private LocalDateTime transactionDate;


    private TransactionStatus status;

    @JsonBackReference
    private AccountDTO account;

    //for transfer

    private String sourceAccount;

    private String destinationAccount;
}
