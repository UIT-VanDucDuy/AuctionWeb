package com.example.auctionweb.repository;


import com.example.auctionweb.entity.Category;
import com.example.auctionweb.entity.Product;
import com.example.auctionweb.entity.Product.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.math.BigDecimal;
import java.util.List;


public interface ProductRepository extends JpaRepository<Product, Integer> {
    // Common
    List<Product> findByStatus(ProductStatus status);
    long countByStatus(ProductStatus status);


    // User facing
    List<Product> findByNameContainingIgnoreCaseAndStatus(String name, ProductStatus status);
    List<Product> findByCategoryAndStatus(Category category, ProductStatus status);
    List<Product> findByNameContainingIgnoreCaseAndCategoryAndStatus(String name, Category category, ProductStatus status);


    // Admin
    List<Product> findAllByOrderByRequestedAtDesc();
    List<Product> findByStatusOrderByRequestedAtDesc(ProductStatus status);
    List<Product> findByCategory(Category category);


    Page<Product> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

    Page<Product> findByNameContainingIgnoreCaseAndCategory_Id(String keyword, Integer categoryId, Pageable pageable);

    Page<Product> findByCategory_Id(Integer categoryId, Pageable pageable);

    // 🆕 Các hàm có lọc giá
    Page<Product> findByStartingPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    Page<Product> findByNameContainingIgnoreCaseAndStartingPriceBetween(String keyword, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    Page<Product> findByCategory_IdAndStartingPriceBetween(Integer categoryId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    Page<Product> findByNameContainingIgnoreCaseAndCategory_IdAndStartingPriceBetween(String keyword, Integer categoryId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    List<Product> findAllByCategory_Id(Integer categoryId);

    @Query("SELECT p FROM Product p WHERE p.seller.id = :sellerId ORDER BY p.requestedAt DESC")
    List<Product> findBySellerId(@Param("sellerId") Integer sellerId);

    List<Product> findTop8ByStatusAndNameContainingIgnoreCaseOrderByNameAsc(
            ProductStatus status, String q);
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.status = :status")
    List<Product> findByCategoryIdAndStatus(@Param("categoryId") Integer categoryId, @Param("status") ProductStatus status);

    @Query("SELECT p FROM Product p WHERE p.status = :status AND " +
            "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Product> findByStatusAndKeyword(@Param("status") ProductStatus status, @Param("keyword") String keyword);

}


