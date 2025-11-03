package com.example.auctionweb.controller;
import com.example.auctionweb.entity.Account;
import com.example.auctionweb.entity.BidHistory;
import com.example.auctionweb.entity.User;
import com.example.auctionweb.service.interfaces.*;
import com.example.auctionweb.websocket.BidWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller
@RequestMapping ("/auction")
public class AuctionController {
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
    @Autowired
    private ICategoryService categoryService;

    @GetMapping("/{id}")
    public ModelAndView loadPage(@PathVariable(name = "id") int id, Authentication authentication) {
        String userName=null;
        if (authentication!=null){
            userName = authentication.getName();
        }
        Account account = accountService.getAccount(userName);
        User user = userService.findUserByAccount(account);
        ModelAndView modelAndView = new ModelAndView("auction/auction");
        modelAndView.addObject("auctionInfo", auctionService.getAuctionInfoById(id));
        modelAndView.addObject("user", user);
        modelAndView.addObject("categories", categoryService.findAll());

        return modelAndView;
    }


}
