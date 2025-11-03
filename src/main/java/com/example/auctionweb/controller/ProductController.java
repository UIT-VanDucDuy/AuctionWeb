package com.example.auctionweb.controller;

import com.example.auctionweb.entity.Account;
import com.example.auctionweb.entity.User;
import com.example.auctionweb.entity.Product;
import com.example.auctionweb.service.IProductService;
import com.example.auctionweb.service.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;

@Controller
@RequestMapping()
public class ProductController {
    @Autowired
    private IProductService productService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IAccountService accountService;
    @Autowired
    private ICategoryService categoryService;

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
        return modelAndView;
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
