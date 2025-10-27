package com.example.auctionweb.controller;

import com.example.auctionweb.entity.Product;
import com.example.auctionweb.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    @Autowired
    private IProductService productService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("products", productService.searchProducts(null, null));
        model.addAttribute("categories", productService.getAllCategories());
        return "home";
    }

    @GetMapping("/search")
    public String searchProducts(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            Model model) {

        model.addAttribute("products", productService.searchProducts(name, categoryId));
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("searchName", name);
        model.addAttribute("selectedCategoryId", categoryId);

        return "home";
    }
}