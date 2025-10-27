package com.example.auctionweb.controller;

import com.example.auctionweb.entity.Product;
import com.example.auctionweb.entity.Category;
import com.example.auctionweb.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private IProductService productService;

    // ========== DASHBOARD ==========

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Product> allProducts = productService.getAllProductsForAdmin();
        List<Product> pendingProducts = productService.getProductsByStatus(Product.ProductStatus.PENDING);
        List<Category> categories = productService.getAllCategories();

        model.addAttribute("totalProducts", allProducts.size());
        model.addAttribute("pendingProducts", pendingProducts.size());
        model.addAttribute("totalCategories", categories.size());
        model.addAttribute("recentProducts", allProducts.stream().limit(5).toList());

        return "admin/dashboard";
    }

    // ========== CRUD SẢN PHẨM ==========

    // READ - Danh sách sản phẩm
    @GetMapping("/products")
    public String productManagement(
            @RequestParam(value = "status", required = false) Product.ProductStatus status,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            Model model) {

        List<Product> products;
        if (status != null && categoryId != null) {
            // Lọc theo cả status và category
            products = productService.getAllProductsForAdmin().stream()
                    .filter(p -> p.getStatus() == status)
                    .filter(p -> p.getCategory() != null && p.getCategory().getId().equals(categoryId))
                    .toList();
        } else if (status != null) {
            products = productService.getProductsByStatus(status);
        } else if (categoryId != null) {
            products = productService.getProductsByCategory(categoryId);
        } else {
            products = productService.getAllProductsForAdmin();
        }

        List<Category> categories = productService.getAllCategories();

        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("statuses", Product.ProductStatus.values());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedCategoryId", categoryId);

        return "admin/products";
    }

    // READ - Chi tiết sản phẩm
    @GetMapping("/products/{id}")
    public String productDetail(@PathVariable Integer id, Model model) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return "redirect:/admin/products";
        }

        List<Category> categories = productService.getAllCategories();

        model.addAttribute("product", product);
        model.addAttribute("categories", categories);
        model.addAttribute("statuses", Product.ProductStatus.values());
        return "admin/product-detail";
    }

    // CREATE - Form thêm sản phẩm
    @GetMapping("/products/add")
    public String addProductForm(Model model) {
        List<Category> categories = productService.getAllCategories();
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categories);
        model.addAttribute("statuses", Product.ProductStatus.values());
        return "admin/product-form";
    }

    // CREATE - Thêm sản phẩm
    @PostMapping("/products")
    public String createProduct(
            @ModelAttribute Product product,
            @RequestParam Integer categoryId,
            RedirectAttributes redirectAttributes) {

        try {
            Category category = productService.getCategoryById(categoryId);
            product.setCategory(category);
            productService.saveProduct(product);
            redirectAttributes.addFlashAttribute("success", "Thêm sản phẩm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm sản phẩm: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }

    // UPDATE - Form chỉnh sửa sản phẩm
    @GetMapping("/products/{id}/edit")
    public String editProductForm(@PathVariable Integer id, Model model) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return "redirect:/admin/products";
        }

        List<Category> categories = productService.getAllCategories();

        model.addAttribute("product", product);
        model.addAttribute("categories", categories);
        model.addAttribute("statuses", Product.ProductStatus.values());
        return "admin/product-form";
    }

    // UPDATE - Cập nhật sản phẩm
    @PostMapping("/products/{id}")
    public String updateProduct(
            @PathVariable Integer id,
            @ModelAttribute Product productUpdate,
            @RequestParam Integer categoryId,
            RedirectAttributes redirectAttributes) {

        try {
            Product existingProduct = productService.getProductById(id);
            if (existingProduct == null) {
                redirectAttributes.addFlashAttribute("error", "Sản phẩm không tồn tại!");
                return "redirect:/admin/products";
            }

            Category category = productService.getCategoryById(categoryId);

            existingProduct.setName(productUpdate.getName());
            existingProduct.setDescription(productUpdate.getDescription());
            existingProduct.setStartingPrice(productUpdate.getStartingPrice());
            existingProduct.setCurrentPrice(productUpdate.getCurrentPrice());
            existingProduct.setImageUrl(productUpdate.getImageUrl());
            existingProduct.setCategory(category);
            existingProduct.setStatus(productUpdate.getStatus());

            productService.saveProduct(existingProduct);
            redirectAttributes.addFlashAttribute("success", "Cập nhật sản phẩm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật sản phẩm: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }

    // UPDATE - Cập nhật trạng thái sản phẩm
    @PostMapping("/products/{id}/status")
    public String updateProductStatus(
            @PathVariable Integer id,
            @RequestParam Product.ProductStatus status,
            RedirectAttributes redirectAttributes) {

        try {
            productService.updateProductStatus(id, status);
            redirectAttributes.addFlashAttribute("success", "Cập nhật trạng thái sản phẩm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật trạng thái: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }

    // DELETE - Xóa sản phẩm
    @PostMapping("/products/{id}/delete")
    public String deleteProduct(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {

        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("success", "Xóa sản phẩm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa sản phẩm: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }

    // ========== CRUD DANH MỤC ==========

    // READ - Danh sách danh mục
    @GetMapping("/categories")
    public String categoryManagement(Model model) {
        List<Category> categories = productService.getAllCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("category", new Category());
        return "admin/categories";
    }

    // READ - Chi tiết danh mục
    @GetMapping("/categories/{id}")
    public String categoryDetail(@PathVariable Integer id, Model model) {
        Category category = productService.getCategoryById(id);
        if (category == null) {
            return "redirect:/admin/categories";
        }

        List<Product> products = productService.getProductsByCategory(id);

        model.addAttribute("category", category);
        model.addAttribute("products", products);
        return "admin/category-detail";
    }

    // CREATE - Thêm danh mục
    @PostMapping("/categories")
    public String createCategory(
            @ModelAttribute Category category,
            RedirectAttributes redirectAttributes) {

        try {
            productService.createCategory(category);
            redirectAttributes.addFlashAttribute("success", "Tạo danh mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi tạo danh mục: " + e.getMessage());
        }

        return "redirect:/admin/categories";
    }

    // UPDATE - Cập nhật danh mục
    @PostMapping("/categories/{id}")
    public String updateCategory(
            @PathVariable Integer id,
            @RequestParam String name,
            RedirectAttributes redirectAttributes) {

        try {
            Category category = new Category();
            category.setName(name);
            productService.updateCategory(id, category);
            redirectAttributes.addFlashAttribute("success", "Cập nhật danh mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật danh mục: " + e.getMessage());
        }

        return "redirect:/admin/categories";
    }

    // DELETE - Xóa danh mục
    @PostMapping("/categories/{id}/delete")
    public String deleteCategory(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {

        try {
            productService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("success", "Xóa danh mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa danh mục: " + e.getMessage());
        }

        return "redirect:/admin/categories";
    }
}