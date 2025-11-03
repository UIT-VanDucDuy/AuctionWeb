package com.example.auctionweb.controller;

import com.example.auctionweb.entity.Account;
import com.example.auctionweb.entity.User;
import com.example.auctionweb.service.interfaces.IAccountService;
import com.example.auctionweb.service.interfaces.IBidHistoryService;
import com.example.auctionweb.service.interfaces.ICategoryService;
import com.example.auctionweb.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @Autowired
    private IUserService userService;
    @Autowired
    private IAccountService accountService;
    @Autowired
    private IBidHistoryService bidHistoryService;
    @Autowired
    private ICategoryService categoryService;
    @GetMapping(value = {"/", "/home"})
    public String showHome(Model model, Authentication authentication){
        // Lấy thông tin user nếu đã login
        String userName = null;
        if (authentication != null){
            userName = authentication.getName();
            Account account = accountService.getAccount(userName);
            User user = userService.findUserByAccount(account);
            model.addAttribute("user", user);
        }
        model.addAttribute("categories", categoryService.findAll());

        return "layout/home";
    }

}