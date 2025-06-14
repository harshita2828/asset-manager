package com.example.ams.datamodels.form.response;


public class CategoryResponseDTO {
    private String id;
    private String name;
    private String description;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CategoryResponseDTO(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}
