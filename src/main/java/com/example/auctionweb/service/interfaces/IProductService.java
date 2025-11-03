package com.example.auctionweb.service;


import com.example.auctionweb.dto.ProductRequestDTO;
import com.example.auctionweb.entity.Category;
import com.example.auctionweb.entity.Product;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;


public interface IProductService {
    // ========= USER-FACING =========
    List<Product> searchProducts(String name, Integer categoryId);
    List<Category> getAllCategories();

    // ========= PRODUCT CRUD =========
    // Create via DTO (preferred)
    Product createProduct(ProductRequestDTO productDTO);
    // Create via entity (compat)
    Product saveProduct(Product product);


    // Read
    List<Product> getAllProductsForAdmin();
    Product getProductById(Integer id);
    List<Product> getProductsByStatus(Product.ProductStatus status);
    List<Product> getProductsByCategory(Integer categoryId);
    List<Product> getProductsBySeller(Integer sellerId);
    List<Product> findAll();
    Product findById(int id);


    // Update
    Product updateProduct(Integer id, ProductRequestDTO productDTO);
    Product updateProductStatus(Integer productId, Product.ProductStatus status);


    // Delete
    void deleteProduct(Integer productId);
    // ========= CATEGORY CRUD =========
    Category createCategory(Category category);
    Category getCategoryById(Integer id);
    Category updateCategory(Integer categoryId, Category category);
    void deleteCategory(Integer categoryId);


    // Utility
    long countProductsByStatus(Product.ProductStatus status);


    Page<Product> searchProducts(String keyword, Integer categoryId, BigDecimal minPrice, BigDecimal maxPrice, int page, int size);

}