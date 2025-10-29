package com.example.auctionweb.controller;

import com.example.auctionweb.entity.Product;
import com.example.auctionweb.entity.Category;
import com.example.auctionweb.repository.CategoryRepository;
import com.example.auctionweb.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("/dashboard")
    public String userDashboard(Model model) {
        List<Product> approvedProducts = productService.getApprovedProducts();
        List<Category> categories = categoryRepository.findAll();

        model.addAttribute("products", approvedProducts);
        model.addAttribute("categories", categories);
        return "user/dashboard";
    }

    @GetMapping("/products")
    public String viewProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer categoryId,
            Model model) {

        List<Product> products;
        Category category = null;

        if (categoryId != null) {
            category = categoryRepository.findById(categoryId).orElse(null);
        }

        products = productService.searchApprovedProducts(search, category);
        List<Category> categories = categoryRepository.findAll();

        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("search", search);
        model.addAttribute("selectedCategoryId", categoryId);

        return "user/products";
    }
}