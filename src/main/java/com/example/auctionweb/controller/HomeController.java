package com.example.auctionweb.controller;

import com.example.auctionweb.entity.Account;
import com.example.auctionweb.entity.User;
import com.example.auctionweb.service.interfaces.IAccountService;
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
    @GetMapping("/home")
    public String showHome(Model model, Authentication authentication){
        String userName=null;
        if (authentication!=null){
            userName = authentication.getName();
        }
        Account account = accountService.getAccount(userName);
        User user = userService.findUserByAccount(account);
        model.addAttribute("user", user);
        return "home";
    }

}