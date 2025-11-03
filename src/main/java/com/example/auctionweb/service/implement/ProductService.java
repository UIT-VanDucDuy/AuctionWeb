package com.example.auctionweb.service.implement;

import com.example.auctionweb.entity.Category;
import com.example.auctionweb.entity.Product;
import com.example.auctionweb.repository.CategoryRepository;
import com.example.auctionweb.repository.ProductRepository;
import com.example.auctionweb.service.interfaces.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    @Override
    public Page<Product> searchProducts(String keyword, Integer categoryId,
                                        BigDecimal minPrice, BigDecimal maxPrice,
                                        int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasCategory = categoryId != null && categoryId > 0;
        boolean hasMin = minPrice != null;
        boolean hasMax = maxPrice != null;

        // Mặc định giá trị khoảng
        if (!hasMin) minPrice = BigDecimal.ZERO;
        if (!hasMax) maxPrice = new BigDecimal("9999999999"); // giá trần lớn

        // ✅ Kết hợp logic tìm kiếm
        if (hasKeyword && hasCategory) {
            return productRepository.findByNameContainingIgnoreCaseAndCategory_IdAndStartingPriceBetween(keyword, categoryId, minPrice, maxPrice, pageable);
        } else if (hasKeyword) {
            return productRepository.findByNameContainingIgnoreCaseAndStartingPriceBetween(keyword, minPrice, maxPrice, pageable);
        } else if (hasCategory) {
            return productRepository.findByCategory_IdAndStartingPriceBetween(categoryId, minPrice, maxPrice, pageable);
        } else if (hasMin || hasMax) {
            return productRepository.findByStartingPriceBetween(minPrice, maxPrice, pageable);
        } else {
            return productRepository.findAll(pageable);
        }
    }

}
