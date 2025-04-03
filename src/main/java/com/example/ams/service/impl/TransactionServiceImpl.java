package com.example.ams.service.impl;

import com.example.ams.dao.AssetRepository;
import com.example.ams.dao.TransactionRepository;
import com.example.ams.entities.Asset;
import com.example.ams.entities.Transaction;
import com.example.ams.entities.TransactionType;
import com.example.ams.form.request.TransactionRequestDTO;
import com.example.ams.form.response.TransactionResponseDTO;
import com.example.ams.service.TransactionService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final AssetRepository assetRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository, AssetRepository assetRepository) {
        this.transactionRepository = transactionRepository;
        this.assetRepository = assetRepository;
    }

    @Override
    public TransactionResponseDTO saveTransaction(TransactionRequestDTO transactionRequestDTO) {
        try {
            if (transactionRequestDTO == null ||
                    transactionRequestDTO.getAssetId() == null ||
                    transactionRequestDTO.getTransactionType() == null ||
                    transactionRequestDTO.getAmount() == null) {
                throw new IllegalArgumentException("Invalid request: All fields are required.");
            }

            Optional<Asset> asset = assetRepository.findById(Long.parseLong(transactionRequestDTO.getAssetId()));

            if (asset.isEmpty()) {
                throw new RuntimeException("Asset not found");
            }

            Transaction transaction = new Transaction();
            transaction.setAsset(asset.get());
            transaction.setTransactionType(TransactionType.valueOf(transactionRequestDTO.getTransactionType()));
            transaction.setAmount(Double.parseDouble(transactionRequestDTO.getAmount()));
            transaction.setTransactionDate(LocalDate.now());

            Transaction savedTransaction = transactionRepository.save(transaction);

            return new TransactionResponseDTO(
                    savedTransaction.getId().toString(),
                    savedTransaction.getAsset().getName(),
                    savedTransaction.getTransactionType().toString(),
                    savedTransaction.getAmount().toString(),
                    savedTransaction.getTransactionDate().toString()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error saving transaction: " + e.getMessage());
        }
    }

    @Override
    public List<TransactionResponseDTO> getAllTransactions() {
        try {
            List<Transaction> transactions = transactionRepository.findAll();

            if (transactions.isEmpty()) {
                throw new RuntimeException("No transactions found.");
            }

            return transactions.stream()
                    .map(transaction -> new TransactionResponseDTO(
                            transaction.getId().toString(),
                            transaction.getAsset().getName(),
                            transaction.getTransactionType().toString(),
                            transaction.getAmount().toString(),
                            transaction.getTransactionDate().toString()
                    ))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error fetching transactions: " + e.getMessage());
        }
    }

    @Override
    public TransactionResponseDTO getTransactionById(String id) {
        Transaction transaction = transactionRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
        return new TransactionResponseDTO(
                transaction.getId().toString(),
                transaction.getAsset().getName(),
                transaction.getTransactionType().toString(),
                transaction.getAmount().toString(),
                transaction.getTransactionDate().toString()
        );

    }

    @Override
    public TransactionResponseDTO updateTransaction(String id, TransactionRequestDTO transactionRequestDTO) {
        Transaction transaction = transactionRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));

        if(transactionRequestDTO.getAssetId() != null) {
            Optional<Asset> asset = assetRepository.findById(Long.parseLong(transactionRequestDTO.getAssetId()));
            asset.ifPresent(transaction::setAsset);
        }

        if(transactionRequestDTO.getTransactionType() != null) {
            transaction.setTransactionType(TransactionType.valueOf(transactionRequestDTO.getTransactionType()));
        }

        if(transactionRequestDTO.getAmount() != null) {
            transaction.setAmount(Double.parseDouble(transactionRequestDTO.getAmount()));
        }

        transaction.setTransactionDate(LocalDate.now());

        Transaction updatedTransaction = transactionRepository.save(transaction);

        return new TransactionResponseDTO(
                updatedTransaction.getId().toString(),
                updatedTransaction.getAsset().getName(),
                updatedTransaction.getTransactionType().toString(),
                updatedTransaction.getAmount().toString(),
                updatedTransaction.getTransactionDate().toString()
        );
    }

    @Override
    public void deleteTransaction(String id) {
        try {
            if (!transactionRepository.existsById(Long.valueOf(id))) {
                throw new RuntimeException("Transaction not found.");
            }
            transactionRepository.deleteById(Long.valueOf(id));
        } catch (Exception e) {
            throw new RuntimeException("Error deleting transaction: " + e.getMessage());
        }
    }
}
