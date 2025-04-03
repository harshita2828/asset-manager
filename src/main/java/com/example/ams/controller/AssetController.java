package com.example.ams.controller;

import com.example.ams.form.request.AssetRequestDTO;
import com.example.ams.form.response.AssetResponseDTO;
import com.example.ams.service.AssetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assets")
public class AssetController {
    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @PostMapping
    public ResponseEntity<?> createAsset(@RequestBody AssetRequestDTO request) {
        try {
            if (request == null || request.getName() == null || request.getType() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid request: Asset name and type cannot be null");
            }

            AssetResponseDTO asset = assetService.saveAsset(request);
            return ResponseEntity.ok(asset);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating asset: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllAssets() {
        try {
            List<AssetResponseDTO> assets = assetService.getAllAssets();
            if (assets == null || assets.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No assets found");
            }
            return ResponseEntity.ok(assets);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching assets: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAsset(@PathVariable String id) {
        try {
            if (id == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Asset ID cannot be null");
            }

            assetService.deleteAsset(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting asset: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAsset(@PathVariable("id") String id, @RequestBody AssetRequestDTO assetRequestDTO) {
        try {
            if(assetRequestDTO == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid request body cannot be null");
            }

            AssetResponseDTO updatedAsset = assetService.updateAsset(id, assetRequestDTO);
            return ResponseEntity.ok(updatedAsset);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating asset : " + e.getMessage());
        }
    }
}
