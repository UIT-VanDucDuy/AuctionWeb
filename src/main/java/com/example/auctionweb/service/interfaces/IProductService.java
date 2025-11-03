package com.example.auctionweb.service.interfaces;

import com.example.auctionweb.entity.Category;
import com.example.auctionweb.entity.Product;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

public interface IProductService {
    Product findById(int id);
    List<Product> findAll();

    Page<Product> searchProducts(String keyword, Integer categoryId, BigDecimal minPrice,BigDecimal maxPrice, int page, int size);
}
