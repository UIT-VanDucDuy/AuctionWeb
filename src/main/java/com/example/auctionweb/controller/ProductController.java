package com.example.auctionweb.controller;

import com.example.auctionweb.entity.Account;
import com.example.auctionweb.entity.User;
import com.example.auctionweb.entity.Product;
import com.example.auctionweb.service.implement.ProductService;
import com.example.auctionweb.service.interfaces.*;
import com.example.auctionweb.repository.CategoryRepository;
import com.example.auctionweb.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;

@Controller
@RequestMapping()
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IAccountService accountService;
    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping(value = "/create")
    public ModelAndView loadPage(Authentication authentication) {
        String userName=null;
        if (authentication!=null){
            userName = authentication.getName();
        }
        Account account = accountService.getAccount(userName);
        User user = userService.findUserByAccount(account);
        ModelAndView modelAndView = new ModelAndView("product/contact");
        modelAndView.addObject("user", user);
        modelAndView.addObject("categories", categoryService.findAll());
        return modelAndView;
    }

    @PostMapping(value = "/create")
    public String createProduct(
            Authentication authentication,
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) BigDecimal startingPrice,
            @RequestParam(required = false) String imageUrl,
            Model model
    ) {
        String username = authentication != null ? authentication.getName() : null;
        Account account = accountService.getAccount(username);
        User user = userService.findUserByAccount(account);

        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setStartingPrice(startingPrice);
        product.setSeller(user);
        product.setRequestedAt(java.time.LocalDateTime.now());
        product.setStatus(Product.ProductStatus.PENDING);

        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId).orElse(null);
            product.setCategory(category);
        }
        if (imageUrl != null) {
            product.setImageUrl(imageUrl);
        }

        productService.saveProduct(product);
        return "redirect:/user/profile?success=1";
    }

    @PostMapping("/products/{id}/update")
    public String updateProduct(
            Authentication authentication,
            @PathVariable Integer id,
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) BigDecimal startingPrice,
            @RequestParam(required = false) String imageUrl
    ) {
        String username = authentication != null ? authentication.getName() : null;
        Account account = accountService.getAccount(username);
        User user = userService.findUserByAccount(account);

        Product product = productService.getProductById(id);
        if (product == null || product.getSeller() == null || !product.getSeller().getId().equals(user.getId())) {
            return "redirect:/user/profile?error=not_owner";
        }

        product.setName(name);
        product.setDescription(description);
        product.setStartingPrice(startingPrice);
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId).orElse(null);
            product.setCategory(category);
        }
        if (imageUrl != null) {
            product.setImageUrl(imageUrl);
        }
        productService.saveProduct(product);
        return "redirect:/user/profile?updated=1";
    }
    @RequestMapping(value = "/search")
    public ModelAndView Search(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Authentication authentication) {

        String userName = null;
        if (authentication != null) {
            userName = authentication.getName();
        }
        Account account = accountService.getAccount(userName);
        User user = userService.findUserByAccount(account);

        Page<Product> productPage = productService.searchProducts(keyword, categoryId, minPrice, maxPrice, page, size);

        ModelAndView modelAndView = new ModelAndView("product/search");
        modelAndView.addObject("user", user);
        modelAndView.addObject("keyword", keyword);
        modelAndView.addObject("categoryId", categoryId);
        modelAndView.addObject("minPrice", minPrice);
        modelAndView.addObject("maxPrice", maxPrice);
        modelAndView.addObject("products", productPage.getContent());
        modelAndView.addObject("currentPage", page);
        modelAndView.addObject("totalPages", productPage.getTotalPages());
        modelAndView.addObject("categories", categoryService.findAll());

        return modelAndView;
    }

}
