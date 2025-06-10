package com.example.ams.controller;

import com.example.ams.datamodels.form.request.AssetRequestDTO;
import com.example.ams.datamodels.form.response.APIResponse;
import com.example.ams.datamodels.form.response.AssetResponseDTO;
import com.example.ams.service.AssetService;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assets")
public class AssetController {
    private static final Logger logger = LoggerFactory.getLogger(AssetController.class);
    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @PostMapping
    public ResponseEntity<APIResponse<AssetResponseDTO>> createAsset(@RequestBody AssetRequestDTO request) {
        logger.info("Inside createAsset in AssetController.");
        try {
            if (ObjectUtils.isEmpty(request) || ObjectUtils.isEmpty(request.getName()) || ObjectUtils.isEmpty(request.getType())) {
                logger.error("Invalid request: Asset name and type cannot be null");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new APIResponse<>(false, "Invalid request: Asset name and type cannot be null", null));
            }
            AssetResponseDTO asset = assetService.saveAsset(request);
            logger.info("Asset created successfully.");
            return ResponseEntity.ok(new APIResponse<>(true, "Asset created successfully", asset));
        } catch (Exception e) {
            logger.error("Error creating asset: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new APIResponse<>(false, "Error creating asset: " + e.getMessage(), null));
        }
    }

    @GetMapping
    public ResponseEntity<APIResponse<List<AssetResponseDTO>>> getAllAssets() {
        logger.info("Inside getAllAssets in AssetController.");
        try {
            List<AssetResponseDTO> assets = assetService.getAllAssets();
            if (assets == null || assets.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(new APIResponse<>(true, "No assets found", null));
            }
            return ResponseEntity.ok(new APIResponse<>(true, "Assets fetched successfully", assets));
        } catch (Exception e) {
            logger.error("Error fetching assets: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new APIResponse<>(false, "Error fetching assets: " + e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> deleteAsset(@PathVariable String id) {
        logger.info("Inside deleteAsset in AssetController.");
        try {
            if (ObjectUtils.isEmpty(id)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new APIResponse<>(false, "Asset ID cannot be null", null));
            }

            assetService.deleteAsset(id);
            logger.info("Asset deleted successfully.");
            return ResponseEntity.ok(new APIResponse<>(true, "Asset deleted successfully", null));
        } catch (Exception e) {
            logger.error("Error deleting asset: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new APIResponse<>(false, "Error deleting asset: " + e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<AssetResponseDTO>> updateAsset(
            @PathVariable("id") String id,
            @RequestBody AssetRequestDTO assetRequestDTO) {
        logger.info("Inside updateAsset in AssetController.");
        try {
            if (ObjectUtils.isEmpty(assetRequestDTO)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new APIResponse<>(false, "Invalid request body", null));
            }

            AssetResponseDTO updatedAsset = assetService.updateAsset(id, assetRequestDTO);
            logger.info("Asset updated successfully.");
            return ResponseEntity.ok(new APIResponse<>(true, "Asset updated successfully", updatedAsset));
        } catch (Exception e) {
            logger.error("Error updating asset: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new APIResponse<>(false, "Error updating asset: " + e.getMessage(), null));
        }
    }
}
