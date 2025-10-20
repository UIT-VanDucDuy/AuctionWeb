package com.example.auctionweb.controller;

import com.example.auctionweb.entity.BidHistory;
import com.example.auctionweb.service.IBidHistoryService;
import com.example.auctionweb.websocket.BidWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping ("/listbid")
public class ListBidController{

    @Autowired
    private IBidHistoryService bidHistoryService;
    @Autowired
    private BidWebSocketHandler bidWebSocketHandler;


    @GetMapping("")
    public ModelAndView showList(@ModelAttribute("bidHistory") BidHistory bidHistory,
                                 Model model){
        ModelAndView modelAndView = new ModelAndView("home");
        modelAndView.addObject("bidHistoryList", bidHistoryService.findAll());
        return modelAndView;
    }
    @PostMapping("")
    public String save(@ModelAttribute("bidHistory") BidHistory bidHistory,
                       RedirectAttributes redirectAttributes) {
        boolean success = bidHistoryService.add(bidHistory);

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
