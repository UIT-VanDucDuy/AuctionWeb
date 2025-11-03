package com.example.auctionweb.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoryRequestDTO {

    private Integer id;

    @NotBlank(message = "Tên danh mục không được để trống")
    @Size(min = 2, max = 100, message = "Tên danh mục phải từ 2 đến 100 ký tự")
    private String name;

    public CategoryRequestDTO() {
    }

    public CategoryRequestDTO(String name) {
        this.name = name;
    }

    // GETTERS AND SETTERS
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}