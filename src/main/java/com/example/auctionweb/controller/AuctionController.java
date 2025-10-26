package com.example.auctionweb.controller;
import com.example.auctionweb.dto.AuctionDto;
import com.example.auctionweb.entity.BidHistory;
import com.example.auctionweb.service.AuctionService;
import com.example.auctionweb.service.IAuctionService;
import com.example.auctionweb.service.IBidHistoryService;
import com.example.auctionweb.websocket.BidWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @GetMapping("/{id}")
    public ModelAndView loadPage(@PathVariable(name = "id") int id, Model model) {
        ModelAndView modelAndView = new ModelAndView("auction");
        modelAndView.addObject("auctionInfo", auctionService.getAuctionInfoById(id));
        return modelAndView;
    }
//    @GetMapping("")
//    public ModelAndView showList(@ModelAttribute("bidHistory") BidHistory bidHistory,
//                                 Model model){
//        ModelAndView modelAndView = new ModelAndView("auction");
//        modelAndView.addObject("history_list", bidHistoryService.findAll());
//        return modelAndView;
//    }
    @PostMapping("")
    public String save(@ModelAttribute("bidHistory") BidHistory bidHistory,
                       RedirectAttributes redirectAttributes) {
        boolean success;
        BidHistory savedBid = bidHistoryService.add(bidHistory);
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
