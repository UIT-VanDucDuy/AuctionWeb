package com.example.auctionweb.service.implement;

import com.example.auctionweb.dto.ProductRequestDTO;
import com.example.auctionweb.entity.Category;
import com.example.auctionweb.entity.Product;
import com.example.auctionweb.repository.CategoryRepository;
import com.example.auctionweb.repository.ProductRepository;
import com.example.auctionweb.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class ProductService implements com.example.auctionweb.service.IProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository,
                          UserRepository userRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
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
    // ========= USER-FACING =========
    @Override
    @Transactional(readOnly = true)
    public List<Product> searchProducts(String name, Integer categoryId) {
        boolean hasName = name != null && !name.isBlank();
        boolean hasCategory = categoryId != null;

        if (hasName && hasCategory) {
            Category category = categoryRepository.findById(categoryId).orElse(null);
            if (category == null) return Collections.emptyList();
            return productRepository.findByNameContainingIgnoreCaseAndCategoryAndStatus(
                    name, category, Product.ProductStatus.APPROVED);
        } else if (hasName) {
            return productRepository.findByNameContainingIgnoreCaseAndStatus(
                    name, Product.ProductStatus.APPROVED);
        } else if (hasCategory) {
            Category category = categoryRepository.findById(categoryId).orElse(null);
            if (category == null) return Collections.emptyList();
            return productRepository.findByCategoryAndStatus(
                    category, Product.ProductStatus.APPROVED);
        } else {
            return productRepository.findByStatus(Product.ProductStatus.APPROVED);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // ========= PRODUCT CRUD =========

    @Override
    public Product createProduct(ProductRequestDTO dto) {
        validateHighestPrice(dto.getStartingPrice(), dto.getHighestPrice());

        Product p = new Product();
        p.setName(dto.getName());
        p.setDescription(dto.getDescription());
        p.setStartingPrice(dto.getStartingPrice());
        p.setRequestedAt(LocalDateTime.now());
        p.setStatus(Product.ProductStatus.PENDING);

        p.setImageUrl(normalizeImageFileName(dto.getImageUrl()));

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId()).orElse(null);
            p.setCategory(category);
        }

        userRepository.findById(1).ifPresent(p::setSeller);

        // KHÔNG set highestPrice vào Product
        return productRepository.save(p);
    }

    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    // READ
    @Override
    @Transactional(readOnly = true)
    public List<Product> getAllProductsForAdmin() {
        return productRepository.findAllByOrderByRequestedAtDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProductById(Integer id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getProductsByStatus(Product.ProductStatus status) {
        return productRepository.findByStatusOrderByRequestedAtDesc(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(Integer categoryId) {
        if (categoryId == null) return Collections.emptyList();
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category == null) return Collections.emptyList();
        return productRepository.findByCategory(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getProductsBySeller(Integer sellerId) {
        return productRepository.findBySellerId(sellerId);
    }

    // UPDATE
    @Override
    public Product updateProductStatus(Integer productId, Product.ProductStatus status) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));
        product.setStatus(status);
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Integer id, ProductRequestDTO dto) {
        // (tuỳ chọn) kiểm tra form-only
        validateHighestPrice(dto.getStartingPrice(), dto.getHighestPrice());

        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));

        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setStartingPrice(dto.getStartingPrice());
        existing.setImageUrl(normalizeImageFileName(dto.getImageUrl()));

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId()).orElse(null);
            existing.setCategory(category);
        } else {
            existing.setCategory(null);
        }

        // KHÔNG set highestPrice vào Product
        return productRepository.save(existing);
    }

    // DELETE
    @Override
    public void deleteProduct(Integer productId) {
        productRepository.deleteById(productId);
    }

    // ========= CATEGORY CRUD =========
    @Override
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    @Transactional(readOnly = true)
    public Category getCategoryById(Integer id) {
        return categoryRepository.findById(id).orElse(null);
    }

    @Override
    public Category updateCategory(Integer categoryId, Category category) {
        Category existing = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục"));
        existing.setName(category.getName());
        return categoryRepository.save(existing);
    }

    @Override
    public void deleteCategory(Integer categoryId) {
        categoryRepository.deleteById(categoryId);
    }

    // ========= Utilities =========
    @Override
    @Transactional(readOnly = true)
    public Product findById(int id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public long countProductsByStatus(Product.ProductStatus status) {
        return productRepository.countByStatus(status);
    }

    private void validateHighestPrice(BigDecimal startingPrice, BigDecimal highestPrice) {
        if (startingPrice == null || highestPrice == null) return;
        if (highestPrice.compareTo(startingPrice) < 0) {
            throw new IllegalArgumentException("Giá cao nhất không được nhỏ hơn giá khởi điểm!");
        }
    }
    private String normalizeImageFileName(String raw) {
        if (raw == null) return null;
        String trimmed = raw.trim();
        if (trimmed.isEmpty()) return null;
        int slash = Math.max(trimmed.lastIndexOf('/'), trimmed.lastIndexOf('\\'));
        return (slash >= 0) ? trimmed.substring(slash + 1) : trimmed;
    }
}
