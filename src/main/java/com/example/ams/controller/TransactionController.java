package com.example.ams.controller;

import com.example.ams.datamodels.constants.Constants;
import com.example.ams.datamodels.form.request.TransactionRequestDTO;
import com.example.ams.datamodels.form.response.APIResponse;
import com.example.ams.datamodels.form.response.TransactionResponseDTO;
import com.example.ams.service.TransactionService;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);
    private final TransactionService transactionService;
    private final Constants constants;

    public TransactionController(TransactionService transactionService, Constants constants) {
        this.transactionService = transactionService;
        this.constants = constants;
    }

    @PostMapping
    public ResponseEntity<APIResponse<TransactionResponseDTO>> createTransaction(@RequestBody TransactionRequestDTO request) {
        logger.info("Inside createTransaction.");
        try {
            if (ObjectUtils.isEmpty(request) ||
                    ObjectUtils.isEmpty(request.getTransactionType()) ||
                    ObjectUtils.isEmpty(request.getAmount())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new APIResponse<>(false, "Invalid request: Transaction type and amount cannot be null", null));
            }

            TransactionResponseDTO transaction = transactionService.saveTransaction(request);
            return ResponseEntity.ok(new APIResponse<>(true, "Transaction created successfully", transaction));
        } catch (Exception e) {
            logger.error("Error creating transaction: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new APIResponse<>(false, "Error creating transaction: " + e.getMessage(), null));
        }
    }

    @GetMapping
    public ResponseEntity<APIResponse<List<TransactionResponseDTO>>> getAllTransactions() {
        logger.info("Inside getAllTransactions.");
        try {
            List<TransactionResponseDTO> transactions = transactionService.getAllTransactions();
            if (transactions == null || transactions.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(new APIResponse<>(true, "No transactions found", null));
            }
            return ResponseEntity.ok(new APIResponse<>(true, "Transactions fetched successfully", transactions));
        } catch (Exception e) {
            logger.error("Error fetching transactions: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new APIResponse<>(false, "Error fetching transactions: " + e.getMessage(), null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<TransactionResponseDTO>> getTransactionById(@PathVariable("id") String id) {
        logger.info("Inside getTransactionById.");
        try {
            TransactionResponseDTO transaction = transactionService.getTransactionById(id);
            return ResponseEntity.ok(new APIResponse<>(true, "Transaction fetched successfully", transaction));
        } catch (RuntimeException e) {
            logger.error(constants.TNX_NOT_FOUND, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new APIResponse<>(false, constants.TNX_NOT_FOUND + e.getMessage(), null));
        } catch (Exception e) {
            logger.error("Error fetching transaction: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new APIResponse<>(false, "Error fetching transaction: " + e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<TransactionResponseDTO>> updateTransaction(
            @PathVariable("id") String id,
            @RequestBody TransactionRequestDTO request) {
        logger.info("Inside updateTransaction.");
        try {
            TransactionResponseDTO updatedTransaction = transactionService.updateTransaction(id, request);
            return ResponseEntity.ok(new APIResponse<>(true, "Transaction updated successfully", updatedTransaction));
        } catch (RuntimeException e) {
            logger.error(constants.TNX_NOT_FOUND, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new APIResponse<>(false, constants.TNX_NOT_FOUND + e.getMessage(), null));
        } catch (Exception e) {
            logger.error("Error updating transaction: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new APIResponse<>(false, "Error updating transaction: " + e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> deleteTransaction(@PathVariable("id") String id) {
        logger.info("Inside deleteTransaction.");
        try {
            transactionService.deleteTransaction(id);
            return ResponseEntity.ok(new APIResponse<>(true, "Transaction deleted successfully", null));
        } catch (RuntimeException e) {
            logger.error(constants.TNX_NOT_FOUND, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new APIResponse<>(false, constants.TNX_NOT_FOUND + e.getMessage(), null));
        } catch (Exception e) {
            logger.error("Error deleting transaction: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new APIResponse<>(false, "Error deleting transaction: " + e.getMessage(), null));
        }
    }
}
