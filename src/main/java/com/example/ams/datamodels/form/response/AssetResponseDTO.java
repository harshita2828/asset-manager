package com.example.ams.datamodels.form.response;

public class AssetResponseDTO {
    private String id;
    private String name;
    private String type;
    private String value;
    private String purchaseDate;
    private String ownerName;
    private String categoryName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public AssetResponseDTO(String id, String name, String type, String value, String purchaseDate, String ownerName, String categoryName) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.value = value;
        this.purchaseDate = purchaseDate;
        this.ownerName = ownerName;
        this.categoryName = categoryName;
    }
}
