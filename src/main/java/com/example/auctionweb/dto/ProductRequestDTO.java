package com.example.auctionweb.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * LƯU Ý:
 * - imageUrl: CHỈ là TÊN FILE trong src/main/resources/static/images
 *   Ví dụ: "iphone15.png". Khi render: @{'/images/' + ${product.imageUrl}}
 * - highestPrice: chỉ tồn tại ở DTO để hiển thị/validate form, KHÔNG map xuống Product.
 * - Không có bất kỳ validation cho imageUrl.
 */
public class ProductRequestDTO {

    private Integer id;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(min = 2, max = 200, message = "Tên sản phẩm phải từ 2 đến 200 ký tự")
    private String name;

    @Size(max = 1000, message = "Mô tả không được vượt quá 1000 ký tự")
    private String description;

    @NotNull(message = "Giá khởi điểm không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá khởi điểm phải lớn hơn 0")
    @Digits(integer = 15, fraction = 2, message = "Giá khởi điểm không hợp lệ")
    private BigDecimal startingPrice;

    // chỉ ở DTO, KHÔNG lưu Product
    @DecimalMin(value = "0.0", inclusive = true, message = "Giá cao nhất phải >= 0")
    @Digits(integer = 15, fraction = 2, message = "Giá cao nhất không hợp lệ")
    private BigDecimal highestPrice;

    @NotNull(message = "Danh mục không được để trống")
    private Integer categoryId;

    // tên file trong static/images, VD: "iphone15.png"
    private String imageUrl;

    public ProductRequestDTO() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getStartingPrice() { return startingPrice; }
    public void setStartingPrice(BigDecimal startingPrice) { this.startingPrice = startingPrice; }

    public BigDecimal getHighestPrice() { return highestPrice; }
    public void setHighestPrice(BigDecimal highestPrice) { this.highestPrice = highestPrice; }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
