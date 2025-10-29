package com.example.auctionweb.controller;

import com.example.auctionweb.dto.ProductRequestDTO;
import com.example.auctionweb.dto.CategoryRequestDTO;
import com.example.auctionweb.entity.*;
import com.example.auctionweb.repository.*;
import com.example.auctionweb.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ProductService productService;

    // ========== DASHBOARD ==========
    @GetMapping("")
    public String dashboard(Model model) {
        long pendingProducts = productService.countProductsByStatus(Product.ProductStatus.PENDING);
        long totalUsers = userRepository.count();
        long totalProducts = productRepository.count();

        model.addAttribute("pendingProducts", pendingProducts);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalProducts", totalProducts);
        return "admin/dashboard";
    }

    // ========== QUẢN LÝ SẢN PHẨM ==========
    @GetMapping("/products")
    public String productManagement(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer categoryId,
            Model model) {

        List<Product> products;
        if (status != null && !status.isEmpty()) {
            Product.ProductStatus productStatus = Product.ProductStatus.valueOf(status.toUpperCase());
            products = productService.getProductsByStatus(productStatus);
        } else if (categoryId != null) {
            products = productService.getProductsByCategory(categoryId);
        } else {
            products = productService.getAllProductsForAdmin();
        }

        List<Category> categories = categoryRepository.findAll();

        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("statuses", Product.ProductStatus.values());
        return "admin/products";
    }

    @GetMapping("/products/add")
    public String showAddProductForm(Model model) {
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("productDTO", new ProductRequestDTO());
        model.addAttribute("categories", categories);
        return "admin/product-form";
    }

    @PostMapping("/products")
    public String createProduct(
            @Valid @ModelAttribute ProductRequestDTO productDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            List<Category> categories = categoryRepository.findAll();
            model.addAttribute("categories", categories);
            return "admin/product-form";
        }

        try {
            productService.createProduct(productDTO);
            redirectAttributes.addFlashAttribute("success", "Thêm sản phẩm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm sản phẩm: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }

    @GetMapping("/products/{id}/edit")
    public String showEditProductForm(@PathVariable Integer id, Model model) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return "redirect:/admin/products";
        }

        ProductRequestDTO productDTO = new ProductRequestDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setStartingPrice(product.getStartingPrice());
        productDTO.setImageUrl(product.getImageUrl());
        productDTO.setCategoryId(product.getCategory() != null ? product.getCategory().getId() : null);

        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("productDTO", productDTO);
        model.addAttribute("categories", categories);
        return "admin/product-form";
    }

    @PostMapping("/products/{id}")
    public String updateProduct(
            @PathVariable Integer id,
            @Valid @ModelAttribute ProductRequestDTO productDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            List<Category> categories = categoryRepository.findAll();
            model.addAttribute("categories", categories);
            return "admin/product-form";
        }

        try {
            productService.updateProduct(id, productDTO);
            redirectAttributes.addFlashAttribute("success", "Cập nhật sản phẩm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật sản phẩm: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }

    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("success", "Xóa sản phẩm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa sản phẩm: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    // ========== DUYỆT SẢN PHẨM ==========
    @GetMapping("/product-approvals")
    public String productApprovals(Model model) {
        List<Product> pendingProducts = productService.getProductsByStatus(Product.ProductStatus.PENDING);
        model.addAttribute("products", pendingProducts);
        return "admin/product-approvals";
    }

    @PostMapping("/products/{id}/approve")
    public String approveProduct(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            productService.updateProductStatus(id, Product.ProductStatus.APPROVED);
            redirectAttributes.addFlashAttribute("success", "Đã duyệt sản phẩm!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi duyệt sản phẩm: " + e.getMessage());
        }
        return "redirect:/admin/product-approvals";
    }

    @PostMapping("/products/{id}/reject")
    public String rejectProduct(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            productService.updateProductStatus(id, Product.ProductStatus.REJECTED);
            redirectAttributes.addFlashAttribute("success", "Đã từ chối sản phẩm!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi từ chối sản phẩm: " + e.getMessage());
        }
        return "redirect:/admin/product-approvals";
    }

    // ========== QUẢN LÝ DANH MỤC ==========
    @GetMapping("/categories")
    public String categoryManagement(Model model) {
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        model.addAttribute("categoryDTO", new CategoryRequestDTO());
        return "admin/categories";
    }

    @PostMapping("/categories")
    public String createCategory(
            @Valid @ModelAttribute CategoryRequestDTO categoryDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Dữ liệu không hợp lệ");
            return "redirect:/admin/categories";
        }

        try {
            Category category = new Category();
            category.setName(categoryDTO.getName());
            categoryRepository.save(category);
            redirectAttributes.addFlashAttribute("success", "Thêm danh mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm danh mục: " + e.getMessage());
        }

        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/{id}/delete")
    public String deleteCategory(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            categoryRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Xóa danh mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa danh mục: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    // ========== QUẢN LÝ NGƯỜI DÙNG ==========
    @GetMapping("/users")
    public String userManagement(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "admin/users";
    }

    @PostMapping("/users/{id}/activate")
    public String activateUser(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            User user = userRepository.findById(id).orElse(null);
            if (user != null && user.getAccount() != null) {
                user.getAccount().setActive(true);
                userRepository.save(user);
                redirectAttributes.addFlashAttribute("success", "Đã kích hoạt người dùng!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi kích hoạt người dùng: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/deactivate")
    public String deactivateUser(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            User user = userRepository.findById(id).orElse(null);
            if (user != null && user.getAccount() != null) {
                user.getAccount().setActive(false);
                userRepository.save(user);
                redirectAttributes.addFlashAttribute("success", "Đã vô hiệu hóa người dùng!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi vô hiệu hóa người dùng: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
}