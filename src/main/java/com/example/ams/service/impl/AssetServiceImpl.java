package com.example.ams.service.impl;

import com.example.ams.dao.AssetRepository;
import com.example.ams.dao.CategoryRepository;
import com.example.ams.dao.UserRepository;
import com.example.ams.datamodels.entities.Asset;
import com.example.ams.datamodels.entities.Category;
import com.example.ams.datamodels.entities.User;
import com.example.ams.datamodels.form.request.AssetRequestDTO;
import com.example.ams.datamodels.form.response.AssetResponseDTO;
import com.example.ams.exceptionhandling.ResourceNotFound;
import com.example.ams.service.AssetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AssetServiceImpl implements AssetService {

    private static final Logger logger = LoggerFactory.getLogger(AssetServiceImpl.class);
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
                logger.error("Invalid request: All fields are required");
                throw new IllegalArgumentException("Invalid request: All fields are required");
            }

            Optional<Asset> existingAsset = assetRepository.findByNameAndTypeAndValue(
                    assetRequestDTO.getName().trim(),
                    assetRequestDTO.getType().trim(),
                    Double.parseDouble(assetRequestDTO.getValue())
            );
            if (existingAsset.isPresent()) {
                logger.error("Asset with same name, type and value akready exists");
                throw new ResourceNotFound("Asset with same name, type, and value already exists.");
            }

            Optional<User> user = userRepository.findById(Long.parseLong(assetRequestDTO.getOwnerId()));
            Optional<Category> category = categoryRepository.findById(Long.parseLong(assetRequestDTO.getCategoryId()));

            if (user.isEmpty() || category.isEmpty()) {
                logger.error("User or category does not exist.");
                throw new ResourceNotFound("User or Category not found");
            }

            Asset asset = new Asset();
            asset.setName(assetRequestDTO.getName().trim());
            asset.setType(assetRequestDTO.getType().trim());
            asset.setValue(Double.parseDouble(assetRequestDTO.getValue()));
            asset.setPurchaseDate(LocalDate.now());
            asset.setOwner(user.get());
            asset.setCategory(category.get());

            Asset savedAsset = assetRepository.save(asset);

            logger.info("Asset saved in database");

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
            throw new ResourceNotFound("Error saving transaction: " + e.getMessage());

        }
    }


    @Override
    public List<AssetResponseDTO> getAllAssets() {
        try {
            List<Asset> assets = assetRepository.findAll();

            logger.info("Assets retrieved");

            if (assets.isEmpty()) {
                logger.error("No assets found.");
                throw new ResourceNotFound("No assets found");
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
            throw new ResourceNotFound("Error fetching assets: " + e.getMessage());
        }
    }

    @Override
    public AssetResponseDTO getAssetById(String id) {
        try {
            Asset asset = assetRepository.findById(Long.valueOf(id))
                    .orElseThrow(() -> new RuntimeException("Asset not found"));
            logger.error("Asset not found with id: {}", id);

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
            logger.error("Error getting asset with id: {}", id);
            throw new ResourceNotFound("Error fetching asset: " + e.getMessage());
        }
    }

    @Override
    public void deleteAsset(String id) {
        try {
            if (!assetRepository.existsById(Long.valueOf(id))) {
                logger.error("Asset not found to delete, id: {}", id);
                throw new ResourceNotFound("Asset not found");
            }
            assetRepository.deleteById(Long.valueOf(id));
            logger.info("Assest deleted with id: {}", id);
        } catch (Exception e) {
            logger.error("Error while deleting asset with id: {}", e.getMessage());
        }
    }

    @Override
    public AssetResponseDTO updateAsset(String id, AssetRequestDTO assetRequestDTO) {
        try {
            Asset existingAsset = assetRepository.findById(Long.parseLong(id))
                    .orElseThrow(() -> {
                        logger.error("Asset not able to update, id not found: {}", id);
                        return new RuntimeException("Asset not found");
                    });

            if (StringUtils.hasText(assetRequestDTO.getName())) {
                existingAsset.setName(assetRequestDTO.getName().trim());
            }

            if (StringUtils.hasText(assetRequestDTO.getType())) {
                existingAsset.setType(assetRequestDTO.getType().trim());
            }

            if (StringUtils.hasText(assetRequestDTO.getValue())) {
                existingAsset.setValue(Double.parseDouble(assetRequestDTO.getValue().trim()));
            }

            updateOwnerIfPresent(assetRequestDTO.getOwnerId(), existingAsset);
            updateCategoryIfPresent(assetRequestDTO.getCategoryId(), existingAsset);

            Asset updatedAsset = assetRepository.save(existingAsset);
            logger.info("Asset updated with id: {}", id);

            return new AssetResponseDTO(
                    updatedAsset.getId().toString(),
                    updatedAsset.getName(),
                    updatedAsset.getType(),
                    updatedAsset.getValue().toString(),
                    updatedAsset.getPurchaseDate().toString(),
                    updatedAsset.getOwner().getName(),
                    updatedAsset.getCategory().getName()
            );
        } catch (Exception e) {
            throw new ResourceNotFound("Error updating asset : " + e.getMessage());
        }
    }

    private void updateOwnerIfPresent(String ownerId, Asset asset) {
        if (StringUtils.hasText(ownerId)) {
            userRepository.findById(Long.parseLong(ownerId))
                    .ifPresentOrElse(
                            asset::setOwner,
                            () -> {
                                logger.error("User not found for asset update with owner id: {}", ownerId);
                                throw new RuntimeException("User not found");
                            });
        }
    }

    private void updateCategoryIfPresent(String categoryId, Asset asset) {
        if (StringUtils.hasText(categoryId)) {
            categoryRepository.findById(Long.parseLong(categoryId))
                    .ifPresentOrElse(
                            asset::setCategory,
                            () -> {
                                logger.error("Category not found for asset update with category id: {}", categoryId);
                                throw new RuntimeException("Category not found");
                            });
        }
    }

}
