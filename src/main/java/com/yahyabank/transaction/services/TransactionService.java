package com.yahyabank.transaction.services;

import com.yahyabank.account.entity.Account;
import com.yahyabank.account.repo.AccountRepo;
import com.yahyabank.auth_users.entity.User;
import com.yahyabank.auth_users.services.UserService;
import com.yahyabank.enums.TransactionStatus;
import com.yahyabank.enums.TransactionType;
import com.yahyabank.exceptions.BadRequestException;
import com.yahyabank.exceptions.InsufficientBalanceException;
import com.yahyabank.exceptions.InvalidTransactionException;
import com.yahyabank.exceptions.NotFoundException;
import com.yahyabank.notification.dtos.NotificationDTO;
import com.yahyabank.notification.services.NotificationService;
import com.yahyabank.response.Response;
import com.yahyabank.transaction.dtos.TransactionDTO;
import com.yahyabank.transaction.dtos.TransactionRequest;
import com.yahyabank.transaction.entity.Transaction;
import com.yahyabank.transaction.repo.TransactionRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.BatchUpdateException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService implements ITransactionService {


    private final TransactionRepo transactionRepo;
    private final AccountRepo accountRepo;
    private final NotificationService notificationService;
    private final ModelMapper modelMapper;
    private final UserService userService;

    @Override
    @Transactional
    public Response<?> createTransaction(TransactionRequest transactionRequest) {
        Transaction transaction = new Transaction();
        transaction.setType(transactionRequest.getTransactionType());
        transaction.setAmount(transactionRequest.getAmount());
        transaction.setDescription(transactionRequest.getDescription());

        switch (transactionRequest.getTransactionType()) {
            case DEPOSIT -> handleDeposit(transactionRequest, transaction);
            case WITHDRAWAL -> handleWithdrawal(transactionRequest, transaction);
            case TRANSFER -> handleTransfer(transactionRequest, transaction);
            default -> throw new InvalidTransactionException("Invalid transaction type");
        }
        transaction.setStatus(TransactionStatus.SUCCESS);

        Transaction savedTxn = transactionRepo.save(transaction);
        sendTransactionNotifications(savedTxn);

        return Response.builder().statusCode(HttpStatus.OK.value()).message("Transaction successfully").build();
    }


    private void handleTransfer(TransactionRequest request, Transaction transaction) {
        Account sourceAccount = accountRepo.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new NotFoundException("Account not found"));

        Account destinationAccount = accountRepo.findByAccountNumber(request.getDestinationAccountNumber())
                .orElseThrow(() -> new NotFoundException("Account not found"));

        if (sourceAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(request.getAmount()));
        accountRepo.save(sourceAccount);

        destinationAccount.setBalance(destinationAccount.getBalance().add(request.getAmount()));
        accountRepo.save(destinationAccount);

        transaction.setAccount(sourceAccount);
        transaction.setSourceAccount(sourceAccount.getAccountNumber());
        transaction.setDestinationAccount(destinationAccount.getAccountNumber());


    }

    private void handleWithdrawal(TransactionRequest request, Transaction transaction) {
        Account account = accountRepo.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new NotFoundException("Account not found"));

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        account.setBalance(account.getBalance().subtract(request.getAmount()));
        transaction.setAccount(account);
        accountRepo.save(account);
    }

    private void handleDeposit(TransactionRequest request, Transaction transaction) {
        Account account = accountRepo.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new NotFoundException("Account not found"));

        account.setBalance(account.getBalance().add(request.getAmount()));
        transaction.setAccount(account);
        accountRepo.save(account);
    }

    @Override
    public Response<List<TransactionDTO>> getTransactionForMyAccount(String accountNumber, int page, int size) {

        User user = userService.getCurrentLoggedInUser();

        Account account = accountRepo.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NotFoundException("Account not found"));

        if (account.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Account dose not belong to the authenticated user");
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDate").descending());

        Page<Transaction> txns = transactionRepo.findByAccount_AccountNumber(accountNumber, pageable);

        List<TransactionDTO> transactionDTOS = txns.getContent()
                .stream()
                .map(transaction -> modelMapper
                        .map(transaction, TransactionDTO.class)).toList();
        return Response.<List<TransactionDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Transactions retrieved")
                .data(transactionDTOS)
                .meta(Map.of("currentPage", txns.getNumber()
                        , "totalItems", txns.getTotalElements()
                        , "totalPages", txns.getTotalPages()
                        , "pageSize", txns.getSize()))

                .build();
    }


    private void sendTransactionNotifications(Transaction txn) {
        User user = txn.getAccount().getUser();
        String subject;
        String template;

        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("name", user.getFirstName());
        templateVariables.put("amount", txn.getAmount());
        templateVariables.put("accountNumber", txn.getAccount().getAccountNumber());
        templateVariables.put("date", txn.getTransactionDate());
        templateVariables.put("balance", txn.getAccount().getBalance());

        if (txn.getType() == TransactionType.DEPOSIT) {
            subject = "Credit Alert";
            template = "credit-alert";

            NotificationDTO notificationEmailToSendOut = NotificationDTO
                    .builder()
                    .recipient(user.getEmail())
                    .subject(subject)
                    .templateName(template)
                    .templateVariables(templateVariables)
                    .build();
            notificationService.sendEmail(notificationEmailToSendOut, user);


        } else if (txn.getType() == TransactionType.WITHDRAWAL) {
            subject = "Debit Alert";
            template = "debit-alert";

            NotificationDTO notificationEmailToSendOut = NotificationDTO
                    .builder().recipient(user.getEmail())
                    .subject(subject).templateName(template)
                    .templateVariables(templateVariables).build();
            notificationService.sendEmail(notificationEmailToSendOut, user);

        } else if (txn.getType() == TransactionType.TRANSFER) {

            subject = "Debit Alert";
            template = "debit-alert";

            NotificationDTO notificationEmailToSendOut = NotificationDTO.builder()
                    .recipient(user.getEmail())
                    .subject(subject).templateName(template)
                    .templateVariables(templateVariables)
                    .build();

            notificationService.sendEmail(notificationEmailToSendOut, user);

            Account destinationAccount = accountRepo.findByAccountNumber(txn.getDestinationAccount())
                    .orElseThrow(() -> new NotFoundException("Destination account not found"));

            User receiver = destinationAccount.getUser();
            Map<String, Object> receiverVar = new HashMap<>();
            receiverVar.put("name", receiver.getFirstName());
            receiverVar.put("amount", txn.getAmount());
            receiverVar.put("accountNumber", destinationAccount.getAccountNumber());
            receiverVar.put("date", txn.getTransactionDate());
            receiverVar.put("balance", destinationAccount.getBalance());

            NotificationDTO notificationEmailToSendOutReceiver = NotificationDTO.builder()
                    .recipient(receiver.getEmail())
                    .subject("Credit Alert")
                    .templateName("credit-alert")
                    .templateVariables(receiverVar)
                    .build();

            notificationService.sendEmail(notificationEmailToSendOutReceiver, receiver);
        }
    }
}
