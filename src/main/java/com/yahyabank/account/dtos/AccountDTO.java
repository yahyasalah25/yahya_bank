package com.yahyabank.account.dtos;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.yahyabank.auth_users.dtos.UserDTO;
import com.yahyabank.enums.AccountStatus;
import com.yahyabank.enums.AccountType;
import com.yahyabank.enums.Currency;
import com.yahyabank.transaction.dtos.TransactionDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountDTO {




    private String AccountNumber;


    private BigDecimal Balance;


    private AccountType accountType;

    @JsonBackReference //THIS WILL NOT BE ADDED  TO THE ACCOUNT DTO
    private UserDTO user;


    private Currency currency;


    private AccountStatus status;

    @JsonManagedReference  //it helps avoid recursion loop by ignoring the AccountDTO WITHING THE TransactionDTO IT WILL BE IGNORED BECAUSE IT IS A BACK REFERENCE
    private List<TransactionDTO> transactions;

    private LocalDateTime closedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
