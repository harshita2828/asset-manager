package com.example.ams.service.impl;

import com.example.ams.dao.AssetRepository;
import com.example.ams.dao.CategoryRepository;
import com.example.ams.dao.UserRepository;
import com.example.ams.entities.Asset;
import com.example.ams.entities.Category;
import com.example.ams.entities.User;
import com.example.ams.form.request.AssetRequestDTO;
import com.example.ams.form.response.AssetResponseDTO;
import com.example.ams.service.AssetService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AssetServiceImpl implements AssetService {
    private final AssetRepository assetRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public AssetServiceImpl(AssetRepository assetRepository, UserRepository userRepository, CategoryRepository categoryRepository) {
        this.assetRepository = assetRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public AssetResponseDTO saveAsset(AssetRequestDTO assetRequestDTO) {
        try {
            if (assetRequestDTO == null ||
                    assetRequestDTO.getName() == null || assetRequestDTO.getName().trim().isEmpty() ||
                    assetRequestDTO.getType() == null || assetRequestDTO.getType().trim().isEmpty() ||
                    assetRequestDTO.getValue() == null || assetRequestDTO.getValue().trim().isEmpty() ||
                    assetRequestDTO.getOwnerId() == null || assetRequestDTO.getOwnerId().trim().isEmpty() ||
                    assetRequestDTO.getCategoryId() == null || assetRequestDTO.getCategoryId().trim().isEmpty()) {
                throw new IllegalArgumentException("Invalid request: All fields are required");
            }

            Optional<Asset> existingAsset = assetRepository.findByNameAndTypeAndValue(
                    assetRequestDTO.getName().trim(),
                    assetRequestDTO.getType().trim(),
                    Double.parseDouble(assetRequestDTO.getValue())
            );
            if (existingAsset.isPresent()) {
                throw new RuntimeException("Asset with same name, type, and value already exists.");
            }

            Optional<User> user = userRepository.findById(Long.parseLong(assetRequestDTO.getOwnerId()));
            Optional<Category> category = categoryRepository.findById(Long.parseLong(assetRequestDTO.getCategoryId()));

            if (user.isEmpty() || category.isEmpty()) {
                throw new RuntimeException("User or Category not found");
            }

            Asset asset = new Asset();
            asset.setName(assetRequestDTO.getName().trim());
            asset.setType(assetRequestDTO.getType().trim());
            asset.setValue(Double.parseDouble(assetRequestDTO.getValue()));
            asset.setPurchaseDate(LocalDate.now());
            asset.setOwner(user.get());
            asset.setCategory(category.get());

            Asset savedAsset = assetRepository.save(asset);

            return new AssetResponseDTO(
                    savedAsset.getId().toString(),
                    savedAsset.getName(),
                    savedAsset.getType(),
                    savedAsset.getValue().toString(),
                    savedAsset.getPurchaseDate().toString(),
                    savedAsset.getOwner().getName(),
                    savedAsset.getCategory().getName()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error saving asset: " + e.getMessage());
        }
    }


    @Override
    public List<AssetResponseDTO> getAllAssets() {
        try {
            List<Asset> assets = assetRepository.findAll();

            if (assets.isEmpty()) {
                throw new RuntimeException("No assets found");
            }

            return assets.stream()
                    .map(asset -> new AssetResponseDTO(
                            asset.getId().toString(),
                            asset.getName(),
                            asset.getType(),
                            asset.getValue().toString(),
                            asset.getPurchaseDate().toString(),
                            asset.getOwner().getName(),
                            asset.getCategory().getName()
                    ))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error fetching assets: " + e.getMessage());
        }
    }

    @Override
    public AssetResponseDTO getAssetById(String id) {
        try {
            Asset asset = assetRepository.findById(Long.valueOf(id))
                    .orElseThrow(() -> new RuntimeException("Asset not found"));

            return new AssetResponseDTO(
                    asset.getId().toString(),
                    asset.getName(),
                    asset.getType(),
                    asset.getValue().toString(),
                    asset.getPurchaseDate().toString(),
                    asset.getOwner().getName(),
                    asset.getCategory().getName()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error fetching asset: " + e.getMessage());
        }
    }

    @Override
    public void deleteAsset(String id) {
        try {
            if (!assetRepository.existsById(Long.valueOf(id))) {
                throw new RuntimeException("Asset not found");
            }
            assetRepository.deleteById(Long.valueOf(id));
        } catch (Exception e) {
            throw new RuntimeException("Error deleting asset: " + e.getMessage());
        }
    }

    @Override
    public AssetResponseDTO updateAsset(String id, AssetRequestDTO assetRequestDTO) {
        try {
            Asset existingAsset = assetRepository.findById(Long.parseLong(id))
                    .orElseThrow(() -> new RuntimeException("Asset not found"));

            if (assetRequestDTO.getName() != null || !assetRequestDTO.getName().trim().isEmpty()) {
                existingAsset.setName(assetRequestDTO.getName().trim());
            }

            if (assetRequestDTO.getType() != null || !assetRequestDTO.getType().trim().isEmpty()) {
                existingAsset.setType(assetRequestDTO.getType());
            }

            if (assetRequestDTO.getValue() != null || !assetRequestDTO.getValue().trim().isEmpty()) {
                existingAsset.setValue(Double.valueOf(assetRequestDTO.getValue()));
            }

            if (assetRequestDTO.getOwnerId() != null && !assetRequestDTO.getOwnerId().trim().isEmpty()) {
                Optional<User> user = userRepository.findById(Long.parseLong(assetRequestDTO.getOwnerId()));
                if (user.isPresent()) {
                    existingAsset.setOwner(user.get());
                } else {
                    throw new RuntimeException("User not found");
                }
            }
            if (assetRequestDTO.getCategoryId() != null || !assetRequestDTO.getCategoryId().trim().isEmpty()) {
                Optional<Category> category = categoryRepository.findById(Long.parseLong(assetRequestDTO.getCategoryId()));
                if (category.isPresent()) {
                    existingAsset.setCategory(category.get());
                } else {
                    throw new RuntimeException("Category not found");
                }
            }

            Asset updateAsset = assetRepository.save(existingAsset);

            return new AssetResponseDTO(
                    updateAsset.getId().toString(),
                    updateAsset.getName(),
                    updateAsset.getType(),
                    updateAsset.getValue().toString(),
                    updateAsset.getPurchaseDate().toString(),
                    updateAsset.getOwner().getName(),
                    updateAsset.getCategory().getName()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error updating asset : " + e.getMessage());
        }
    }
}
