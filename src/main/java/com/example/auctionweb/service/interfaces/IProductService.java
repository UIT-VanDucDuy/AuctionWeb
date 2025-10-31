package com.example.auctionweb.service.interfaces;

import com.example.auctionweb.entity.Product;

import java.util.List;

public interface IProductService {
    Product findById(int id);
    List<Product> findAll();
}
