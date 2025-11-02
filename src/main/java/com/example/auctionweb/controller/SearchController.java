package com.example.auctionweb.controller;

import com.example.auctionweb.entity.Account;
import com.example.auctionweb.entity.User;
import com.example.auctionweb.service.interfaces.IAccountService;
import com.example.auctionweb.service.interfaces.IAuctionService;
import com.example.auctionweb.service.interfaces.IBidHistoryService;
import com.example.auctionweb.service.interfaces.IUserService;
import com.example.auctionweb.websocket.BidWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/search")
public class SearchController {
    @Autowired
    private IBidHistoryService bidHistoryService;
    @Autowired
    private BidWebSocketHandler bidWebSocketHandler;
    @Autowired
    private IAuctionService auctionService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IAccountService accountService;

    @PostMapping()
    public ModelAndView loadPage(Authentication authentication) {
        String userName=null;
        if (authentication!=null){
            userName = authentication.getName();
        }
        Account account = accountService.getAccount(userName);
        User user = userService.findUserByAccount(account);
        ModelAndView modelAndView = new ModelAndView("product/search");
        modelAndView.addObject("user", user);
        return modelAndView;
    }
}
