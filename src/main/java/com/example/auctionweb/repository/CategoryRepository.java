package com.example.auctionweb.repository;

import com.example.auctionweb.entity.Category;
import com.example.auctionweb.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findAllByOrderByNameAsc();
    Category findByName(String name);
    List<Category> findAll();

    @Query("SELECT c, COUNT(p) FROM Category c LEFT JOIN Product p ON p.category = c GROUP BY c")
    List<Object[]> findAllWithProductCount();

    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.status = :status")
    List<Product> findByCategoryIdAndStatus(@Param("categoryId") Integer categoryId, @Param("status") Product.ProductStatus status);
}