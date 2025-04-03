package com.example.ams.controller;

import com.example.ams.form.request.TransactionRequestDTO;
import com.example.ams.form.response.TransactionResponseDTO;
import com.example.ams.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<?> createTransaction(@RequestBody TransactionRequestDTO request) {
        try {
            if (request == null || request.getTransactionType() == null || request.getAmount() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid request: Transaction type and amount cannot be null");
            }

            TransactionResponseDTO transaction = transactionService.saveTransaction(request);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating transaction: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllTransactions() {
        try {
            List<TransactionResponseDTO> transactions = transactionService.getAllTransactions();
            if (transactions == null || transactions.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No transactions found");
            }
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching transactions: " + e.getMessage());
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable("id") String id) {
        try {
            transactionService.deleteTransaction(id);
            return ResponseEntity.ok("Transaction deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transaction not found: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting transaction: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransaction(@PathVariable("id") String id, @RequestBody TransactionRequestDTO transactionRequestDTO) {
        try {
            TransactionResponseDTO updateTransaction = transactionService.updateTransaction(id, transactionRequestDTO);
            return ResponseEntity.ok(updateTransaction);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transaction not found: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating transaction: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTransactionById(@PathVariable("id") String id) {
        try {
            TransactionResponseDTO transactionResponseDTO = transactionService.getTransactionById(id);
            return ResponseEntity.ok(transactionResponseDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transaction not found: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching transaction: " + e.getMessage());
        }
    }
}
