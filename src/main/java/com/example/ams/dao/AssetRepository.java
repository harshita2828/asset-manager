package com.example.ams.dao;

import com.example.ams.datamodels.entities.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    Optional<Asset> findByNameAndTypeAndValue(String trim, String trim1, double v);
}
