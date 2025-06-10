package com.example.ams.service;

import com.example.ams.datamodels.form.request.TransactionRequestDTO;
import com.example.ams.datamodels.form.response.TransactionResponseDTO;
import java.util.List;

public interface TransactionService {
    TransactionResponseDTO saveTransaction(TransactionRequestDTO transactionRequestDTO);
    List<TransactionResponseDTO> getAllTransactions();
    TransactionResponseDTO getTransactionById(String id);
    TransactionResponseDTO updateTransaction(String id, TransactionRequestDTO transactionRequestDTO);
    void deleteTransaction(String id);

}
