package com.example.auctionweb.repository;

import com.example.auctionweb.entity.Category;
import com.example.auctionweb.entity.Product;
import com.example.auctionweb.entity.Product.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByStatus(ProductStatus status);
    long countByStatus(ProductStatus status);
    //List<Product> findByStatus(ProductStatus status);
    // Cho người dùng
    List<Product> findByNameContainingIgnoreCaseAndStatus(String name, Product.ProductStatus status);
    List<Product> findByCategoryAndStatus(Category category, Product.ProductStatus status);
    List<Product> findByNameContainingIgnoreCaseAndCategoryAndStatus(
            String name, Category category, Product.ProductStatus status);


    // Cho admin - CRUD
    List<Product> findAllByOrderByRequestedAtDesc();
    List<Product> findByStatusOrderByRequestedAtDesc(Product.ProductStatus status);
    List<Product> findByCategory(Category category);

    @Query("SELECT p FROM Product p WHERE p.seller.id = :sellerId ORDER BY p.requestedAt DESC")
    List<Product> findBySellerId(@Param("sellerId") Integer sellerId);
}



