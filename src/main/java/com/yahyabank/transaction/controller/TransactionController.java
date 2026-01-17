/**
 * CREATED BY YAHYA SALAH
 * Date :1/14/2026
 * Time :11:23 AM
 * Project Name:yahyabank
 */
package com.yahyabank.transaction.controller;

import com.yahyabank.response.Response;
import com.yahyabank.transaction.dtos.TransactionRequest;
import com.yahyabank.transaction.services.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<Response<?>> createTransaction(@RequestBody @Valid TransactionRequest request) {
        return ResponseEntity.ok(transactionService.createTransaction(request));
    }


    @GetMapping("/{accountNumber}")
    public ResponseEntity<Response<?>> getTransactionForMyAccount(
            @PathVariable String accountNumber,
            @RequestParam (defaultValue = "0") int page,
            @RequestParam (defaultValue = "50") int size) {
        return ResponseEntity.ok(transactionService.getTransactionForMyAccount(accountNumber,page,size));
    }
}
