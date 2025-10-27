package com.example.auctionweb.service;

import com.example.auctionweb.entity.Product;
import com.example.auctionweb.entity.Category;
import java.util.List;

public interface IProductService {

    // ========== CHO NGƯỜI DÙNG ==========
    List<Product> searchProducts(String name, Integer categoryId);
    List<Category> getAllCategories();

    // ========== CRUD SẢN PHẨM ==========
    // CREATE
    Product saveProduct(Product product);

    // READ
    List<Product> getAllProductsForAdmin();
    Product getProductById(Integer id);
    List<Product> getProductsByStatus(Product.ProductStatus status);
    List<Product> getProductsByCategory(Integer categoryId);
    List<Product> getProductsBySeller(Integer sellerId);

    // UPDATE
    Product updateProductStatus(Integer productId, Product.ProductStatus status);

    // DELETE
    void deleteProduct(Integer productId);

    // ========== CRUD DANH MỤC ==========
    // CREATE
    Category createCategory(Category category);

    // READ
    Category getCategoryById(Integer id);

    // UPDATE
    Category updateCategory(Integer categoryId, Category category);

    // DELETE
    void deleteCategory(Integer categoryId);
}