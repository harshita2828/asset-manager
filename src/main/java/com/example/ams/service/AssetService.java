package com.example.ams.service;

import com.example.ams.form.request.AssetRequestDTO;
import com.example.ams.form.response.AssetResponseDTO;
import java.util.List;

public interface AssetService {
    AssetResponseDTO saveAsset(AssetRequestDTO assetRequestDTO);
    List<AssetResponseDTO> getAllAssets();
    AssetResponseDTO getAssetById(String id);
    void deleteAsset(String id);
    AssetResponseDTO updateAsset(String id, AssetRequestDTO assetRequestDTO);
}
