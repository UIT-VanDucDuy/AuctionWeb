package com.example.auctionweb.controller;
import com.example.auctionweb.entity.Account;
import com.example.auctionweb.entity.BidHistory;
import com.example.auctionweb.entity.User;
import com.example.auctionweb.entity.AuctionRegistration;
import com.example.auctionweb.service.interfaces.*;
import com.example.auctionweb.websocket.BidWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.auctionweb.repository.AuctionRegistrationRepository;
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
    @Autowired
    private AuctionRegistrationRepository auctionRegistrationRepository;

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
        modelAndView.addObject("listRegistration",auctionRegistrationRepository.findByAuction_Id(id));
        return modelAndView;
    }


    @PostMapping("/{id}/register")
    public String registerForAuction(@PathVariable(name = "id") int id,
                                     Authentication authentication,
                                     RedirectAttributes redirectAttributes) {
        String userName=null;
        if (authentication!=null){
            userName = authentication.getName();
        }
        Account account = accountService.getAccount(userName);
        User user = userService.findUserByAccount(account);

        if (user == null) {
            return "redirect:/login";
        }

        boolean already = auctionRegistrationRepository.existsByAuction_IdAndUser_Id(id, user.getId());
        if (already) {
            redirectAttributes.addFlashAttribute("message", "Bạn đã đăng ký tham gia phiên đấu giá này.");
            return "redirect:/auction/" + id;
        }

        AuctionRegistration reg = new AuctionRegistration();
        reg.setAuction(auctionService.getAuctionById(id));
        reg.setUser(user);
        auctionRegistrationRepository.save(reg);
        redirectAttributes.addFlashAttribute("message", "Đăng ký tham gia đấu giá thành công. Vui lòng chờ duyệt.");
        return "redirect:/auction/" + id;
    }
}
