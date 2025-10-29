package com.example.auctionweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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