package com.example.auctionweb.controller;

import com.example.auctionweb.entity.Account;
import com.example.auctionweb.entity.Auction;
import com.example.auctionweb.entity.User;
import com.example.auctionweb.service.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {
    @Autowired
    private IUserService userService;
    @Autowired
    private IAccountService accountService;;
    @Autowired
    private IAuctionService auctionService;
    @Autowired
    private ICategoryService categoryService;

    @GetMapping(value = {"/", "/home"})
    public String showHome(Model model, Authentication authentication){
        String userName = null;
        if (authentication != null){
            userName = authentication.getName();
            Account account = accountService.getAccount(userName);
            User user = userService.findUserByAccount(account);
            model.addAttribute("user", user);
        }
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("auctionsPending",auctionService.getAuctionsByStatus("PENDING"));
        model.addAttribute("auctions8",auctionService.getAuctionsByCategory(8));
        model.addAttribute("auctions2",auctionService.getAuctionsByCategory(2));
        model.addAttribute("auctions6",auctionService.getAuctionsByCategory(6));
        return "layout/home";
    }

    @GetMapping("/403")
    public String accessDenied() {
        return "error/403";
    }

}