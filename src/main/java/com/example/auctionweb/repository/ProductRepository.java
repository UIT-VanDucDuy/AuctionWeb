package com.example.auctionweb.repository;

import com.example.auctionweb.entity.Product;
import com.example.auctionweb.entity.Product.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByStatus(ProductStatus status);
}



