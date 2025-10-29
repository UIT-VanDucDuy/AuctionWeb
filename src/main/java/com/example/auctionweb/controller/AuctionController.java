package com.example.auctionweb.controller;
import com.example.auctionweb.entity.Account;
import com.example.auctionweb.entity.BidHistory;
import com.example.auctionweb.entity.User;
import com.example.auctionweb.service.interfaces.IAccountService;
import com.example.auctionweb.service.interfaces.IAuctionService;
import com.example.auctionweb.service.interfaces.IBidHistoryService;
import com.example.auctionweb.service.interfaces.IUserService;
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

    @GetMapping("/{id}")
    public ModelAndView loadPage(@PathVariable(name = "id") int id, Authentication authentication) {
        String userName=null;
        if (authentication!=null){
            userName = authentication.getName();
        }
        Account account = accountService.getAccount(userName);
        User user = userService.findUserByAccount(account);
        ModelAndView modelAndView = new ModelAndView("auction");
        modelAndView.addObject("auctionInfo", auctionService.getAuctionInfoById(id));
        modelAndView.addObject("user", user);
        return modelAndView;
    }
    @PostMapping("")
    public String save(@ModelAttribute("bidHistory") BidHistory bidHistory,
                       RedirectAttributes redirectAttributes) {
        boolean success;
        BidHistory savedBid = bidHistoryService.save(bidHistory);
        if (savedBid != null && savedBid.getId() != null) {
            success = true;
        } else {
            success = false;
        }
        if (success) {
            try {
                bidWebSocketHandler.broadcastNewBid(bidHistory);
            } catch (Exception e) {
                e.printStackTrace();
            }
            redirectAttributes.addFlashAttribute("mess", "Add success");
        } else {
            redirectAttributes.addFlashAttribute("mess", "Add failed");
        }

        return "redirect:/listbid";
    }

}
