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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService implements IProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    // ========== CHO NGƯỜI DÙNG ==========
    @Override
    public List<Product> searchProducts(String name, Integer categoryId) {
        if (name != null && !name.isEmpty() && categoryId != null) {
            Category category = categoryRepository.findById(categoryId).orElse(null);
            return productRepository.findByNameContainingIgnoreCaseAndCategoryAndStatus(name, category, Product.ProductStatus.APPROVED);
        } else if (name != null && !name.isEmpty()) {
            return productRepository.findByNameContainingIgnoreCaseAndStatus(name, Product.ProductStatus.APPROVED);
        } else if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId).orElse(null);
            return productRepository.findByCategoryAndStatus(category, Product.ProductStatus.APPROVED);
        } else {
            return productRepository.findByStatus(Product.ProductStatus.APPROVED);
        }
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // ========== CRUD SẢN PHẨM ==========
    // CREATE
    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    // READ
    @Override
    public List<Product> getAllProductsForAdmin() {
        return productRepository.findAllByOrderByRequestedAtDesc();
    }

    @Override
    public Product getProductById(Integer id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public List<Product> getProductsByStatus(Product.ProductStatus status) {
        return productRepository.findByStatusOrderByRequestedAtDesc(status);
    }

    @Override
    public List<Product> getProductsByCategory(Integer categoryId) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category != null) {
            return productRepository.findByCategory(category);
        }
        return List.of();
    }

    @Override
    public List<Product> getProductsBySeller(Integer sellerId) {
        return productRepository.findBySellerId(sellerId);
    }

    // UPDATE
    @Override
    public Product updateProductStatus(Integer productId, Product.ProductStatus status) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            throw new RuntimeException("Không tìm thấy sản phẩm");
        }
        product.setStatus(status);
        return productRepository.save(product);
    }

    // DELETE
    @Override
    public void deleteProduct(Integer productId) {
        productRepository.deleteById(productId);
    }

    // ========== CRUD DANH MỤC ==========
    // CREATE
    @Override
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    // READ
    @Override
    public Category getCategoryById(Integer id) {
        return categoryRepository.findById(id).orElse(null);
    }

    // UPDATE
    @Override
    public Category updateCategory(Integer categoryId, Category category) {
        Category existingCategory = categoryRepository.findById(categoryId).orElse(null);
        if (existingCategory == null) {
            throw new RuntimeException("Không tìm thấy danh mục");
        }
        existingCategory.setName(category.getName());
        return categoryRepository.save(existingCategory);
    }

    // DELETE
    @Override
    public void deleteCategory(Integer categoryId) {
        categoryRepository.deleteById(categoryId);
    }

    @Override
    public Product findById(int id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    // ========== CÁC METHOD BỔ SUNG ==========

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

    public Product createProduct(ProductRequestDTO productDTO) {
        // Validate highestPrice từ DTO (nếu có)
        validateHighestPrice(productDTO.getStartingPrice(), productDTO.getHighestPrice());

        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setStartingPrice(productDTO.getStartingPrice());
        product.setImageUrl(productDTO.getImageUrl());
        product.setRequestedAt(LocalDateTime.now());
        product.setStatus(Product.ProductStatus.PENDING);

        if (productDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(productDTO.getCategoryId()).orElse(null);
            product.setCategory(category);
        }

        Optional<User> defaultSeller = userRepository.findById(1);
        defaultSeller.ifPresent(product::setSeller);

        // KHÔNG set highestPrice vào entity vì bạn không muốn sửa entity
        // Nếu sau này có field trong entity, chỉ cần mở comment:
        // product.setHighestPrice(productDTO.getHighestPrice());

        return productRepository.save(product);
    }


    public Product updateProduct(Integer id, ProductRequestDTO productDTO) {
        Product existingProduct = productRepository.findById(id).orElse(null);
        if (existingProduct == null) {
            throw new RuntimeException("Không tìm thấy sản phẩm");
        }

        // Validate highestPrice từ DTO (nếu có)
        validateHighestPrice(productDTO.getStartingPrice(), productDTO.getHighestPrice());

        existingProduct.setName(productDTO.getName());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setStartingPrice(productDTO.getStartingPrice());
        existingProduct.setImageUrl(productDTO.getImageUrl());

        if (productDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(productDTO.getCategoryId()).orElse(null);
            existingProduct.setCategory(category);
        }

        // KHÔNG set highestPrice vào entity (đúng yêu cầu)
        // Nếu sau này bổ sung field trong entity:
        // existingProduct.setHighestPrice(productDTO.getHighestPrice());

        return productRepository.save(existingProduct);
    }
    private void validateHighestPrice(BigDecimal startingPrice, BigDecimal highestPrice) {
        if (startingPrice == null || highestPrice == null) return; // cho phép bỏ trống
        if (highestPrice.compareTo(startingPrice) < 0) {
            throw new IllegalArgumentException("Giá cao nhất không được nhỏ hơn giá khởi điểm!");
        }
    }



    // Đếm sản phẩm theo trạng thái
    public long countProductsByStatus(Product.ProductStatus status) {
        return productRepository.findByStatus(status).size();
    }
}