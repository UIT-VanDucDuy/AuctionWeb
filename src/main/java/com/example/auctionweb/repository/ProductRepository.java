package com.example.auctionweb.repository;

import com.example.auctionweb.entity.Product;
import com.example.auctionweb.entity.Product.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByStatus(ProductStatus status);
    long countByStatus(ProductStatus status);

    Page<Product> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

    Page<Product> findByNameContainingIgnoreCaseAndCategory_Id(String keyword, Integer categoryId, Pageable pageable);

    Page<Product> findByCategory_Id(Integer categoryId, Pageable pageable);

    // 🆕 Các hàm có lọc giá
    Page<Product> findByStartingPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    Page<Product> findByNameContainingIgnoreCaseAndStartingPriceBetween(String keyword, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    Page<Product> findByCategory_IdAndStartingPriceBetween(Integer categoryId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    Page<Product> findByNameContainingIgnoreCaseAndCategory_IdAndStartingPriceBetween(String keyword, Integer categoryId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

}



