package com.example.ams.service.impl;

import com.example.ams.dao.AssetRepository;
import com.example.ams.dao.TransactionRepository;
import com.example.ams.entities.Asset;
import com.example.ams.entities.Transaction;
import com.example.ams.entities.TransactionType;
import com.example.ams.form.request.TransactionRequestDTO;
import com.example.ams.form.response.TransactionResponseDTO;
import com.example.ams.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);
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
                logger.error("Invalid request: All fields are required.");
                throw new IllegalArgumentException("Invalid request: All fields are required.");
            }

            Optional<Asset> asset = assetRepository.findById(Long.parseLong(transactionRequestDTO.getAssetId()));

            if (asset.isEmpty()) {
                logger.error("Asset not found for ID: {}", transactionRequestDTO.getAssetId());
                throw new RuntimeException("Asset not found");
            }

            Transaction transaction = new Transaction();
            transaction.setAsset(asset.get());
            transaction.setTransactionType(TransactionType.valueOf(transactionRequestDTO.getTransactionType()));
            transaction.setAmount(Double.parseDouble(transactionRequestDTO.getAmount()));
            transaction.setTransactionDate(LocalDate.now());

            Transaction savedTransaction = transactionRepository.save(transaction);

            logger.info("Transaction saved successfully with ID: {}", savedTransaction.getId());

            return new TransactionResponseDTO(
                    savedTransaction.getId().toString(),
                    savedTransaction.getAsset().getName(),
                    savedTransaction.getTransactionType().toString(),
                    savedTransaction.getAmount().toString(),
                    savedTransaction.getTransactionDate().toString()
            );
        } catch (Exception e) {
            logger.error("Error saving transaction: {}", e.getMessage());
            throw new RuntimeException("Error saving transaction: " + e.getMessage());
        }
    }

    @Override
    public List<TransactionResponseDTO> getAllTransactions() {
        try {
            List<Transaction> transactions = transactionRepository.findAll();

            if (transactions.isEmpty()) {
                logger.error("Transaction not found");
                throw new RuntimeException("No transactions found.");
            }

            logger.info("Transcations retrieved successfully");

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
            logger.error("Error fetching transactions: {}", e.getMessage());
            throw new RuntimeException("Error fetching transactions: " + e.getMessage());
        }
    }

    @Override
    public TransactionResponseDTO getTransactionById(String id) {
        try {
            Optional<Transaction> transaction = transactionRepository.findById(Long.valueOf(id));

            if (transaction.isEmpty()) {
                logger.error("Transaction not found with ID: {}", id);
                throw new RuntimeException("Transaction not found.");
            }

            logger.info("Transaction retrieved successfully with ID: {}", id);

            return new TransactionResponseDTO(
                    transaction.get().getId().toString(),
                    transaction.get().getAsset().getName(),
                    transaction.get().getTransactionType().toString(),
                    transaction.get().getAmount().toString(),
                    transaction.get().getTransactionDate().toString()
            );
        } catch (Exception e) {
            logger.error("Error fetching transaction: {}", e.getMessage());
            throw new RuntimeException("Error fetching transaction: " + e.getMessage());
        }
    }


    @Override
    public TransactionResponseDTO updateTransaction(String id, TransactionRequestDTO transactionRequestDTO) {
        try {
            logger.info("Starting update for transaction with ID: {}", id);

            Transaction transaction = transactionRepository.findById(Long.parseLong(id))
                    .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));

            logger.info("Transaction with ID: {} found.", id);

            if (transactionRequestDTO.getAssetId() != null) {
                Optional<Asset> asset = assetRepository.findById(Long.parseLong(transactionRequestDTO.getAssetId()));
                if (asset.isPresent()) {
                    transaction.setAsset(asset.get());
                    logger.info("Asset updated for transaction ID: {}. New asset: {}", id, asset.get().getName());
                } else {
                    logger.warn("Asset not found with ID: {} for transaction ID: {}", transactionRequestDTO.getAssetId(), id);
                }
            }

            if (transactionRequestDTO.getTransactionType() != null) {
                transaction.setTransactionType(TransactionType.valueOf(transactionRequestDTO.getTransactionType()));
                logger.info("Transaction type updated for transaction ID: {}. New type: {}", id, transaction.getTransactionType());
            }

            if (transactionRequestDTO.getAmount() != null) {
                transaction.setAmount(Double.parseDouble(transactionRequestDTO.getAmount()));
                logger.info("Amount updated for transaction ID: {}. New amount: {}", id, transaction.getAmount());
            }

            transaction.setTransactionDate(LocalDate.now());
            logger.info("Transaction date updated for transaction ID: {}. New date: {}", id, transaction.getTransactionDate());

            Transaction updatedTransaction = transactionRepository.save(transaction);

            logger.info("Transaction with ID: {} updated successfully.", id);

            return new TransactionResponseDTO(
                    updatedTransaction.getId().toString(),
                    updatedTransaction.getAsset().getName(),
                    updatedTransaction.getTransactionType().toString(),
                    updatedTransaction.getAmount().toString(),
                    updatedTransaction.getTransactionDate().toString()
            );
        } catch (Exception e) {
            logger.error("Error updating transaction with ID: {}. Error: {}", id, e.getMessage());
            throw new RuntimeException("Error updating transaction: " + e.getMessage());
        }
    }


    @Override
    public void deleteTransaction(String id) {
        try {
            if (!transactionRepository.existsById(Long.valueOf(id))) {
                logger.error("Transaction not found with ID: {}", id);
                throw new RuntimeException("Transaction not found.");
            }
            transactionRepository.deleteById(Long.valueOf(id));

            logger.info("Transaction with ID: {} deleted successfully", id);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting transaction: " + e.getMessage());
        }
    }
}
