package com.example.auctionweb.controller;

import com.example.auctionweb.entity.Account;
import com.example.auctionweb.entity.AuctionRegistration;
import com.example.auctionweb.entity.AuctionRegistration.RegistrationStatus;
import com.example.auctionweb.entity.Notification;
import com.example.auctionweb.repository.AccountRepository;
import com.example.auctionweb.repository.AuctionRegistrationRepository;
import com.example.auctionweb.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class AdminController {
    @Autowired
    private AuctionRegistrationRepository registrationRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @GetMapping("/admin/registrations")
    public String listRegistrations(Model model) {
        List<AuctionRegistration> pending = registrationRepository.findByStatus(RegistrationStatus.PENDING);
        model.addAttribute("registrations", pending);
        return "WEB-INF/views/Registrations";
    }

    @PostMapping("/admin/registrations/{id}/approve")
    public String approveRegistration(@PathVariable("id") Integer id) {
        AuctionRegistration reg = registrationRepository.findById(id).orElse(null);
        if (reg != null) {
            reg.setStatus(RegistrationStatus.APPROVED);
            registrationRepository.save(reg);

            Notification n = new Notification();
            n.setUser(reg.getUser());
            n.setNotification("Đăng ký tham gia đấu giá đã được duyệt");
            notificationRepository.save(n);
        }
        return "redirect:/admin/registrations";
    }

    @PostMapping("/admin/registrations/{id}/reject")
    public String rejectRegistration(@PathVariable("id") Integer id) {
        AuctionRegistration reg = registrationRepository.findById(id).orElse(null);
        if (reg != null) {
            reg.setStatus(RegistrationStatus.REJECTED);
            registrationRepository.save(reg);

            Notification n = new Notification();
            n.setUser(reg.getUser());
            n.setNotification("Đăng ký tham gia đấu giá đã bị từ chối");
            notificationRepository.save(n);
        }
        return "redirect:/admin/registrations";
    }

    @GetMapping("/admin/accounts")
    public String listAccounts(Model model) {
        List<Account> accounts = accountRepository.findAll();
        model.addAttribute("accounts", accounts);
        return "WEB-INF/views/Accounts";
    }
}



