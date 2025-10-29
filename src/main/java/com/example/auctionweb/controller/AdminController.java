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
        model.addAttribute("pendingAuctions", registrationRepository.findByStatus(RegistrationStatus.PENDING).size());
        model.addAttribute("pendingProducts", productRepository.findByStatus(ProductStatus.PENDING).size());
        model.addAttribute("totalUsers", userRepository.findAll().size());
        return "admin/management";
    }

    @GetMapping("/management")
    public String dashboard(Model model) {
        model.addAttribute("pendingAuctions", registrationRepository.findByStatus(RegistrationStatus.PENDING).size());
        model.addAttribute("pendingProducts", productRepository.findByStatus(ProductStatus.PENDING).size());
        model.addAttribute("totalUsers", userRepository.findAll().size());
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
    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "admin/users";
    }

    @PostMapping("/users/{id}/activate")
    public String activateUser(@PathVariable Integer id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null && user.getAccount() != null) {
            Account acc = user.getAccount();
            acc.setActive(true);
            accountRepository.save(acc);
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/deactivate")
    public String deactivateUser(@PathVariable Integer id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null && user.getAccount() != null) {
            Account acc = user.getAccount();
            acc.setActive(false);
            accountRepository.save(acc);
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



