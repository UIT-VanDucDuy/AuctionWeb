package com.example.auctionweb.controller;
import com.example.auctionweb.entity.BidHistory;
import com.example.auctionweb.service.interfaces.IBidHistoryService;
import com.example.auctionweb.websocket.BidWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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
        ModelAndView modelAndView = new ModelAndView("home1");
        modelAndView.addObject("bidHistoryList", bidHistoryService.findAll());
        return modelAndView;
    }
    @PostMapping("")
    public String save(@ModelAttribute("bidHistory") BidHistory bidHistory,
                       RedirectAttributes redirectAttributes) {
        // Sử dụng save() thay vì add()
        BidHistory savedBid = bidHistoryService.save(bidHistory);
        boolean success = savedBid != null && savedBid.getId() != null;

        if (success) {
            try {
                // ✅ broadcast chỉ khi lưu thành công
                bidWebSocketHandler.broadcastNewBid(savedBid);
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
