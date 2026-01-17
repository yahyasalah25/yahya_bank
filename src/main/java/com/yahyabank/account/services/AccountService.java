package com.yahyabank.account.services;

import com.yahyabank.account.dtos.AccountDTO;
import com.yahyabank.account.entity.Account;
import com.yahyabank.account.repo.AccountRepo;
import com.yahyabank.auth_users.entity.User;
import com.yahyabank.auth_users.services.UserService;
import com.yahyabank.enums.AccountStatus;
import com.yahyabank.enums.AccountType;
import com.yahyabank.enums.Currency;
import com.yahyabank.exceptions.NotFoundException;
import com.yahyabank.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AccountService implements IAccountService {

    private final AccountRepo accountRepo;
    private final UserService userService;
    private final ModelMapper modelMapper;

    private final Random random = new Random();

    @Override
    public Account createAccount(AccountType accountType, User user) {

        log.info("inside createAccount()");

        String accountNumber = generateAccountNumber();

        Account account = Account.builder()
                .accountNumber(accountNumber)
                .accountType(accountType)
                .user(user)
                .currency(Currency.EGP)
                .balance(BigDecimal.ZERO)
                .status(AccountStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
        return accountRepo.save(account);
    }


    @Override
    public Response<List<AccountDTO>> getMyAccounts() {
        User user = userService.getCurrentLoggedInUser();
        List<AccountDTO> accounts = accountRepo.findByUserId(user.getId())
                .stream()
                .map(account -> modelMapper.map(account, AccountDTO.class))
                .toList();
        return Response.<List<AccountDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("")
                .data(accounts)
                .build();
    }

    @Override
    public Response<?> closeAccount(String accountNumber) {
        User user = userService.getCurrentLoggedInUser();
        Account account = accountRepo.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NotFoundException("Account not Found"));


        if (!user.getAccounts().contains(account)) {
            throw new NotFoundException("Account doesn't belong to you");
        } else if (account.getBalance().compareTo(BigDecimal.ZERO) > 0) {

            throw new NotFoundException("Account balance must be zero before closing");
        }
        account.setStatus(AccountStatus.CLOSED);
        account.setClosedAt(LocalDateTime.now());

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Account Closed Successfully")
                .build();
    }


    private String generateAccountNumber() {

        String accountNumber;


        do {

            accountNumber = "66" + (random.nextInt(9000000) + 1000000);

        } while (accountRepo.findByAccountNumber(accountNumber).isPresent());

        log.info("generate account number:{}",accountNumber);

        return accountNumber;
    }


}
