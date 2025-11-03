package com.example.auctionweb.service.implement;

import com.example.auctionweb.entity.Category;
import com.example.auctionweb.repository.CategoryRepository;
import com.example.auctionweb.service.interfaces.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CategoryService implements ICategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }
}
