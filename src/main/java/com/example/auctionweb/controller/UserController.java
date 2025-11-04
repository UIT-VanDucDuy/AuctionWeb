package com.example.auctionweb.controller;

import com.example.auctionweb.entity.Account;
import com.example.auctionweb.entity.User;
import com.example.auctionweb.repository.UserRepository;
import com.example.auctionweb.repository.AccountRepository;
import com.example.auctionweb.repository.BidHistoryRepository;
import com.example.auctionweb.repository.NotificationRepository;
import com.example.auctionweb.service.interfaces.IAccountService;
import com.example.auctionweb.service.interfaces.IUserService;
import com.example.auctionweb.service.implement.ProductService;
import com.example.auctionweb.service.interfaces.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IAccountService accountService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BidHistoryRepository bidHistoryRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @GetMapping("/profile")
    public String viewProfile(Authentication authentication, Model model) {
        String username = authentication != null ? authentication.getName() : null;
        Account account = accountService.getAccount(username);
        User user = userService.findUserByAccount(account);
        model.addAttribute("user", user);
        if (user != null) {
            model.addAttribute("myProducts", productService.getProductsBySeller(user.getId()));
            model.addAttribute("bidHistory", bidHistoryRepository.findByUserOrderByTimeDesc(user));
            model.addAttribute("notifications", notificationRepository.findByUserOrderByTimeDesc(user));
        } else {
            model.addAttribute("bidHistory", java.util.Collections.emptyList());
            model.addAttribute("notifications", java.util.Collections.emptyList());
        }
        model.addAttribute("categories", categoryService.findAll());
        return "user/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(
            Authentication authentication,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String address
    ) {
        String username = authentication != null ? authentication.getName() : null;
        Account account = accountService.getAccount(username);
        User user = userService.findUserByAccount(account);

        if (user != null) {
            if (name != null) user.setName(name.trim());
            if (email != null) user.setEmail(email.trim());
            if (phone != null) user.setPhone(phone.trim());
            if (address != null) user.setAddress(address.trim());
            userRepository.save(user);
        }
        return "redirect:/user/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(
            Authentication authentication,
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam(required = false) String confirmPassword,
            RedirectAttributes redirectAttributes
    ) {
        String username = authentication != null ? authentication.getName() : null;
        Account account = accountService.getAccount(username);
        if (account == null) {
            redirectAttributes.addFlashAttribute("pwd_error", "Không tìm thấy tài khoản");
            return "redirect:/user/profile";
        }
        if (!passwordEncoder.matches(currentPassword, account.getPassword())) {
            redirectAttributes.addFlashAttribute("pwd_error", "Mật khẩu hiện tại không đúng");
            return "redirect:/user/profile";
        }
        if (confirmPassword != null && !newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("pwd_error", "Xác nhận mật khẩu không khớp");
            return "redirect:/user/profile";
        }
        if (newPassword.length() < 6) {
            redirectAttributes.addFlashAttribute("pwd_error", "Mật khẩu mới phải từ 6 ký tự");
            return "redirect:/user/profile";
        }
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
        return "redirect:/user/profile?pwd_changed=1";
    }
}