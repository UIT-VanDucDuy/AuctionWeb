package com.example.auctionweb.service;

import com.example.auctionweb.dto.ProductRequestDTO;
import com.example.auctionweb.entity.Category;
import com.example.auctionweb.entity.Product;
import com.example.auctionweb.entity.User;
import com.example.auctionweb.repository.CategoryRepository;
import com.example.auctionweb.repository.ProductRepository;
import com.example.auctionweb.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class ProductService implements IProductService {

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

    /** Create via DTO (preferred). highestPrice chỉ tồn tại ở DTO, KHÔNG persist xuống Product. */
    @Override
    public Product createProduct(ProductRequestDTO dto) {
        // (tuỳ chọn) kiểm tra form-only: highestPrice >= startingPrice — không lưu xuống entity
        validateHighestPrice(dto.getStartingPrice(), dto.getHighestPrice());

        Product p = new Product();
        p.setName(dto.getName());
        p.setDescription(dto.getDescription());
        p.setStartingPrice(dto.getStartingPrice());
        p.setRequestedAt(LocalDateTime.now());
        p.setStatus(Product.ProductStatus.PENDING);

        // ảnh: lưu CHỈ tên file (vd: iphone15.png). View render qua /images/<filename>
        p.setImageUrl(normalizeImageFileName(dto.getImageUrl()));

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId()).orElse(null);
            p.setCategory(category);
        }

        // gán seller mặc định nếu cần (tùy business của bạn). Có thể bỏ dòng này.
        userRepository.findById(1).ifPresent(p::setSeller);

        // KHÔNG set highestPrice vào Product
        return productRepository.save(p);
    }

    /** Create via entity (giữ để tương thích cũ). */
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

    /** Cho phép bỏ trống; nếu nhập thì yêu cầu highest >= starting. Chỉ kiểm tra ở DTO (không persist). */
    private void validateHighestPrice(BigDecimal startingPrice, BigDecimal highestPrice) {
        if (startingPrice == null || highestPrice == null) return;
        if (highestPrice.compareTo(startingPrice) < 0) {
            throw new IllegalArgumentException("Giá cao nhất không được nhỏ hơn giá khởi điểm!");
        }
    }

    /** Nhận vào URL hoặc path, chỉ giữ lại phần tên file để lưu (vd: \"iphone15.png\"). */
    private String normalizeImageFileName(String raw) {
        if (raw == null) return null;
        String trimmed = raw.trim();
        if (trimmed.isEmpty()) return null;
        int slash = Math.max(trimmed.lastIndexOf('/'), trimmed.lastIndexOf('\\'));
        return (slash >= 0) ? trimmed.substring(slash + 1) : trimmed;
    }
}
