package com.example.auctionweb.service;

import com.example.auctionweb.entity.Product;
import com.example.auctionweb.entity.Category;
import com.example.auctionweb.repository.ProductRepository;
import com.example.auctionweb.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService implements IProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    // ========== CHO NGƯỜI DÙNG ==========

    @Override
    public List<Product> searchProducts(String name, Integer categoryId) {
        if ((name == null || name.trim().isEmpty()) && categoryId == null) {
            return productRepository.findByStatus(Product.ProductStatus.APPROVED);
        }

        if (categoryId == null) {
            return productRepository.findByNameContainingIgnoreCaseAndStatus(
                    name, Product.ProductStatus.APPROVED);
        }

        if (name == null || name.trim().isEmpty()) {
            Category category = categoryRepository.findById(categoryId).orElse(null);
            if (category != null) {
                return productRepository.findByCategoryAndStatus(category, Product.ProductStatus.APPROVED);
            }
            return List.of();
        }

        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category != null) {
            return productRepository.findByNameContainingIgnoreCaseAndCategoryAndStatus(
                    name, category, Product.ProductStatus.APPROVED);
        }

        return List.of();
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAllByOrderByNameAsc();
    }

    // ========== CRUD SẢN PHẨM ==========

    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

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

    @Override
    public Product updateProductStatus(Integer productId, Product.ProductStatus status) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            product.setStatus(status);
            return productRepository.save(product);
        }
        throw new RuntimeException("Product not found with id: " + productId);
    }

    @Override
    public void deleteProduct(Integer productId) {
        if (!productRepository.existsById(productId)) {
            throw new RuntimeException("Product not found with id: " + productId);
        }
        productRepository.deleteById(productId);
    }

    // ========== CRUD DANH MỤC ==========

    @Override
    public Category createCategory(Category category) {
        if (categoryRepository.findByName(category.getName()) != null) {
            throw new RuntimeException("Category name already exists: " + category.getName());
        }
        return categoryRepository.save(category);
    }

    @Override
    public Category getCategoryById(Integer id) {
        return categoryRepository.findById(id).orElse(null);
    }

    @Override
    public Category updateCategory(Integer categoryId, Category category) {
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));

        Category duplicateCategory = categoryRepository.findByName(category.getName());
        if (duplicateCategory != null && !duplicateCategory.getId().equals(categoryId)) {
            throw new RuntimeException("Category name already exists: " + category.getName());
        }

        existingCategory.setName(category.getName());
        return categoryRepository.save(existingCategory);
    }

    @Override
    public void deleteCategory(Integer categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));

        List<Product> productsInCategory = productRepository.findByCategory(category);
        if (!productsInCategory.isEmpty()) {
            throw new RuntimeException("Cannot delete category with existing products");
        }

        categoryRepository.delete(category);
    }
}