package com.example.auctionweb.service;

import com.example.auctionweb.entity.Product;

import java.util.List;

public interface IProductService {
    Product findById(int id);
    List<Product> findAll();
}
