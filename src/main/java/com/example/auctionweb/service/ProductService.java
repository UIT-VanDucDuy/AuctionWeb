package com.example.auctionweb.service;

import com.example.auctionweb.dto.ProductRequestDTO;
import com.example.auctionweb.entity.Category;
import com.example.auctionweb.entity.Product;
import com.example.auctionweb.entity.User;
import com.example.auctionweb.repository.CategoryRepository;
import com.example.auctionweb.repository.ProductRepository;
import com.example.auctionweb.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    // ========== CHO ADMIN ==========

    // Lấy tất cả sản phẩm (sắp xếp mới nhất)
    public List<Product> getAllProductsForAdmin() {
        return productRepository.findAllByOrderByRequestedAtDesc();
    }

    // Lấy sản phẩm theo trạng thái (sắp xếp mới nhất)
    public List<Product> getProductsByStatus(Product.ProductStatus status) {
        return productRepository.findByStatusOrderByRequestedAtDesc(status);
    }

    // Lấy sản phẩm theo danh mục
    public List<Product> getProductsByCategory(Integer categoryId) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category != null) {
            return productRepository.findByCategory(category);
        }
        return List.of();
    }

    // Lấy sản phẩm theo người bán
    public List<Product> getProductsBySeller(Integer sellerId) {
        return productRepository.findBySellerId(sellerId);
    }

    // ========== CHO NGƯỜI DÙNG ==========

    // Tìm kiếm sản phẩm đã duyệt theo tên
    public List<Product> searchApprovedProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCaseAndStatus(name, Product.ProductStatus.APPROVED);
    }

    // Lấy sản phẩm đã duyệt theo danh mục
    public List<Product> getApprovedProductsByCategory(Category category) {
        return productRepository.findByCategoryAndStatus(category, Product.ProductStatus.APPROVED);
    }

    // Tìm kiếm nâng cao
    public List<Product> searchApprovedProducts(String name, Category category) {
        if (name != null && category != null) {
            return productRepository.findByNameContainingIgnoreCaseAndCategoryAndStatus(name, category, Product.ProductStatus.APPROVED);
        } else if (name != null) {
            return searchApprovedProductsByName(name);
        } else if (category != null) {
            return getApprovedProductsByCategory(category);
        } else {
            return getApprovedProducts();
        }
    }

    // Lấy tất cả sản phẩm đã duyệt
    public List<Product> getApprovedProducts() {
        return productRepository.findByStatus(Product.ProductStatus.APPROVED);
    }

    // ========== CRUD CHUNG ==========

    public Product getProductById(Integer id) {
        return productRepository.findById(id).orElse(null);
    }

    public Product createProduct(ProductRequestDTO productDTO) {
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setStartingPrice(productDTO.getStartingPrice());
        product.setImageUrl(productDTO.getImageUrl());
        product.setRequestedAt(LocalDateTime.now());
        product.setStatus(Product.ProductStatus.PENDING);

        // Set category
        if (productDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(productDTO.getCategoryId()).orElse(null);
            product.setCategory(category);
        }

        // Set default seller (có thể điều chỉnh theo logic đăng nhập)
        Optional<User> defaultSeller = userRepository.findById(1);
        defaultSeller.ifPresent(product::setSeller);

        return productRepository.save(product);
    }

    public Product updateProduct(Integer id, ProductRequestDTO productDTO) {
        Product existingProduct = productRepository.findById(id).orElse(null);
        if (existingProduct == null) {
            throw new RuntimeException("Không tìm thấy sản phẩm");
        }

        existingProduct.setName(productDTO.getName());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setStartingPrice(productDTO.getStartingPrice());
        existingProduct.setImageUrl(productDTO.getImageUrl());

        if (productDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(productDTO.getCategoryId()).orElse(null);
            existingProduct.setCategory(category);
        }

        return productRepository.save(existingProduct);
    }

    public void deleteProduct(Integer id) {
        productRepository.deleteById(id);
    }

    public Product updateProductStatus(Integer id, Product.ProductStatus status) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) {
            throw new RuntimeException("Không tìm thấy sản phẩm");
        }
        product.setStatus(status);
        return productRepository.save(product);
    }

    // Đếm sản phẩm theo trạng thái
    public long countProductsByStatus(Product.ProductStatus status) {
        return productRepository.findByStatus(status).size();
    }
}