package com.example.auctionweb.controller;

import com.example.auctionweb.entity.*;
import com.example.auctionweb.entity.AuctionRegistration.RegistrationStatus;
import com.example.auctionweb.entity.Product.ProductStatus;
import com.example.auctionweb.repository.*;
import com.example.auctionweb.websocket.NotificationWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AuctionRegistrationRepository registrationRepository;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationWebSocketHandler notificationWs;

    // Dashboard
    @GetMapping("")
    public String dashboardRoot(Model model) {
        long pendingAuctionRegistrations = registrationRepository.countByStatus(RegistrationStatus.PENDING);
        long pendingProductRegistrations = productRepository.countByStatus(ProductStatus.PENDING);
        long totalUsers = userRepository.count();
        
        model.addAttribute("pendingAuctions", pendingAuctionRegistrations);
        model.addAttribute("pendingProducts", pendingProductRegistrations);
        model.addAttribute("totalUsers", totalUsers);
        return "admin/management";
    }

    @GetMapping("/management")
    public String dashboard(Model model) {
        long pendingAuctionRegistrations = registrationRepository.countByStatus(RegistrationStatus.PENDING);
        long pendingProductRegistrations = productRepository.countByStatus(ProductStatus.PENDING);
        long totalUsers = userRepository.count();
        
        model.addAttribute("pendingAuctions", pendingAuctionRegistrations);
        model.addAttribute("pendingProducts", pendingProductRegistrations);
        model.addAttribute("totalUsers", totalUsers);
        return "admin/management";
    }

    // Auction registrations list + filter
    @GetMapping("/auction-registrations")
    public String listAuctionRegistrations(@RequestParam(defaultValue = "PENDING") String status, Model model) {
        List<AuctionRegistration> registrations;
        if ("ALL".equalsIgnoreCase(status)) {
            registrations = registrationRepository.findAll();
        } else {
            registrations = registrationRepository.findByStatus(RegistrationStatus.valueOf(status.toUpperCase()));
        }
        model.addAttribute("registrations", registrations);
        model.addAttribute("currentStatus", status.toUpperCase());
        return "admin/auction-registrations";
    }

    @PostMapping("/auction-registrations/{id}/approve")
    public String approveAuctionRegistration(@PathVariable("id") Integer id) throws Exception {
        AuctionRegistration reg = registrationRepository.findById(id).orElse(null);
        if (reg != null) {
            reg.setStatus(RegistrationStatus.APPROVED);
            registrationRepository.save(reg);

            Notification n = new Notification();
            n.setUser(reg.getUser());
            n.setNotification("Đăng ký tham gia đấu giá đã được duyệt");
            n.setTime(LocalDateTime.now());
            notificationRepository.save(n);
            notificationWs.broadcastNotification(n);
        }
        return "redirect:/admin/auction-registrations";
    }

    @PostMapping("/auction-registrations/{id}/reject")
    public String rejectAuctionRegistration(@PathVariable("id") Integer id) throws Exception {
        AuctionRegistration reg = registrationRepository.findById(id).orElse(null);
        if (reg != null) {
            reg.setStatus(RegistrationStatus.REJECTED);
            registrationRepository.save(reg);

            Notification n = new Notification();
            n.setUser(reg.getUser());
            n.setNotification("Đăng ký tham gia đấu giá đã bị từ chối");
            n.setTime(LocalDateTime.now());
            notificationRepository.save(n);
            notificationWs.broadcastNotification(n);
        }
        return "redirect:/admin/auction-registrations";
    }

    // Product registrations list + filter
    @GetMapping("/product-registrations")
    public String listProductRegistrations(@RequestParam(defaultValue = "PENDING") String status, Model model) {
        List<Product> products;
        if ("ALL".equalsIgnoreCase(status)) {
            products = productRepository.findAll();
        } else {
            products = productRepository.findByStatus(ProductStatus.valueOf(status.toUpperCase()));
        }
        model.addAttribute("products", products);
        model.addAttribute("currentStatus", status.toUpperCase());
        return "admin/product-registrations";
    }

    @PostMapping("/product-registrations/{id}/approve")
    public String approveProduct(@PathVariable("id") Integer id) throws Exception {
        Product p = productRepository.findById(id).orElse(null);
        if (p != null) {
            p.setStatus(ProductStatus.APPROVED);
            productRepository.save(p);

            Notification n = new Notification();
            n.setUser(p.getSeller());
            n.setNotification("Sản phẩm của bạn đã được duyệt");
            n.setTime(LocalDateTime.now());
            notificationRepository.save(n);
            notificationWs.broadcastNotification(n);
        }
        return "redirect:/admin/product-registrations";
    }

    @PostMapping("/product-registrations/{id}/reject")
    public String rejectProduct(@PathVariable("id") Integer id) throws Exception {
        Product p = productRepository.findById(id).orElse(null);
        if (p != null) {
            p.setStatus(ProductStatus.REJECTED);
            productRepository.save(p);

            Notification n = new Notification();
            n.setUser(p.getSeller());
            n.setNotification("Sản phẩm của bạn đã bị từ chối");
            n.setTime(LocalDateTime.now());
            notificationRepository.save(n);
            notificationWs.broadcastNotification(n);
        }
        return "redirect:/admin/product-registrations";
    }

    // Users management page
    // Users list - Chỉ hiển thị USER và SELLER, ẩn ADMIN
    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> allUsers = userRepository.findAll();
        
        // Lọc bỏ tài khoản ADMIN - Admin không cần quản lý admin
        List<User> users = allUsers.stream()
                .filter(user -> user.getAccount() != null && user.getAccount().getRole() != Account.Role.ADMIN)
                .toList();
        
        model.addAttribute("users", users);
        return "admin/users";
    }

    @PostMapping("/users/{id}/activate")
    public String activateUser(@PathVariable Integer id,    RedirectAttributes redirectAttributes) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null && user.getAccount() != null) {
            Account acc = user.getAccount();
            
            // Không cho phép kích hoạt tài khoản ADMIN (vì admin không nên bị khóa)
            if (acc.getRole() == Account.Role.ADMIN) {
                redirectAttributes.addFlashAttribute("error", "Không thể thao tác với tài khoản Admin!");
                return "redirect:/admin/users";
            }
            
            acc.setActive(true);
            accountRepository.save(acc);
            redirectAttributes.addFlashAttribute("success", "Đã kích hoạt tài khoản thành công!");
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/deactivate")
    public String deactivateUser(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null && user.getAccount() != null) {
            Account acc = user.getAccount();
            
            // KHÔNG CHO PHÉP KHÓA TÀI KHOẢN ADMIN - Bảo vệ quan trọng!
            if (acc.getRole() == Account.Role.ADMIN) {
                redirectAttributes.addFlashAttribute("error", "Không thể khóa tài khoản Admin!");
                return "redirect:/admin/users";
            }
            
            acc.setActive(false);
            accountRepository.save(acc);
            redirectAttributes.addFlashAttribute("success", "Đã khóa tài khoản thành công!");
        }
        return "redirect:/admin/users";
    }

    // Accounts fallback (optional)
    @GetMapping("/accounts")
    public String listAccounts(Model model) {
        List<Account> accounts = accountRepository.findAll();
        model.addAttribute("accounts", accounts);
        return "admin/users";
    }
}



