package com.example.auctionweb.service;

import com.example.auctionweb.entity.Product;
import com.example.auctionweb.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ProductService implements IProductService {
    @Autowired
    private ProductRepository productRepository;
    @Override
    public Product findById(int id) {
        return productRepository.findById(id).get();
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }
}
