package com.example.auctionweb.controller;

import com.example.auctionweb.entity.Account;
import com.example.auctionweb.entity.User;
import com.example.auctionweb.repository.AccountRepository;
import com.example.auctionweb.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegisterController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("account", new Account());
        return "auth/signup";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                          @RequestParam String password,
                          @RequestParam(required = false) String confirmPassword,
                          @RequestParam String name,
                          @RequestParam(required = false) String email,
                          @RequestParam(required = false) String phone,
                          RedirectAttributes redirectAttributes) {
        try {
            // Kiểm tra username hợp lệ
            if (username == null || username.trim().length() < 3) {
                redirectAttributes.addFlashAttribute("error", "Tên đăng nhập phải có ít nhất 3 ký tự!");
                return "redirect:/register";
            }

            // Kiểm tra username đã tồn tại
            if (accountRepository.findByUsername(username) != null) {
                redirectAttributes.addFlashAttribute("error", "Username đã tồn tại! Vui lòng chọn username khác.");
                return "redirect:/register";
            }

            // Kiểm tra password
            if (password == null || password.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự!");
                return "redirect:/register";
            }

            // Kiểm tra xác nhận mật khẩu
            if (confirmPassword != null && !password.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "Mật khẩu xác nhận không khớp!");
                return "redirect:/register";
            }

            // Tạo Account với password mã hóa
            Account account = new Account();
            account.setUsername(username.trim());
            account.setPassword(passwordEncoder.encode(password)); // Mã hóa password bằng BCrypt
            account.setRole(Account.Role.ROLE_USER); // Mặc định là USER
            account.setActive(true); // Tự động kích hoạt
            Account savedAccount = accountRepository.save(account);

            // Tạo User
            User user = new User();
            user.setName(name != null ? name.trim() : null);
            user.setEmail(email != null && !email.trim().isEmpty() ? email.trim() : null);
            user.setPhone(phone != null && !phone.trim().isEmpty() ? phone.trim() : null);
            user.setAccount(savedAccount);
            userRepository.save(user);

            redirectAttributes.addFlashAttribute("success", "Đăng ký thành công! Bạn có thể đăng nhập ngay bây giờ.");
            return "redirect:/login";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Đã có lỗi xảy ra: " + e.getMessage());
            return "redirect:/register";
        }
    }

    // Helper endpoint để reset password admin
    @GetMapping("/reset-admin-password")
    @ResponseBody
    public String resetAdminPassword() {
        try {
            Account existingAdmin = accountRepository.findByUsername("admin");
            if (existingAdmin == null) {
                return "❌ Admin account not found! <a href='/create-admin-account'>Create new admin</a>";
            }
            
            // Reset password về admin123
            existingAdmin.setPassword(passwordEncoder.encode("admin123"));
            accountRepository.save(existingAdmin);
            
            return "✅ Admin password has been RESET!<br>" +
                   "Username: admin<br>" +
                   "Password: admin123<br>" +
                   "<br><a href='/login'>Go to Login</a>";
        } catch (Exception e) {
            return "❌ Error: " + e.getMessage();
        }
    }
    
    // Helper endpoint để tạo admin account nếu cần
    @GetMapping("/create-admin-account")
    @ResponseBody
    public String createAdminAccount() {
        try {
            // Kiểm tra xem đã có admin chưa
            Account existingAdmin = accountRepository.findByUsername("admin");
            if (existingAdmin != null) {
                return "⚠️ Admin account already exists!<br>" +
                       "<a href='/reset-admin-password'>Reset Admin Password to 'admin123'</a><br>" +
                       "<a href='/login'>Go to Login</a>";
            }

            // Tạo Admin Account
            Account admin = new Account();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123")); // Mã hóa password
            admin.setRole(Account.Role.ROLE_ADMIN);
            admin.setActive(true);
            accountRepository.save(admin);

            // Tạo Admin User
            User adminUser = new User();
            adminUser.setName("Administrator");
            adminUser.setEmail("admin@auction.com");
            adminUser.setPhone("0123456789");
            adminUser.setAccount(admin);
            userRepository.save(adminUser);

            return "✅ Admin account created successfully!<br>" +
                   "Username: admin<br>" +
                   "Password: admin123<br>" +
                   "<a href='/login'>Go to Login</a>";
        } catch (Exception e) {
            return "❌ Error creating admin: " + e.getMessage();
        }
    }

    // Helper endpoint để tạo test user
    @GetMapping("/create-test-user")
    @ResponseBody
    public String createTestUser(@RequestParam(required = false, defaultValue = "testuser") String username,
                                  @RequestParam(required = false, defaultValue = "123") String password) {
        try {
            // Kiểm tra username đã tồn tại
            if (accountRepository.findByUsername(username) != null) {
                return "❌ Username '" + username + "' already exists!";
            }

            // Tạo Account
            Account account = new Account();
            account.setUsername(username);
            account.setPassword(passwordEncoder.encode(password));
            account.setRole(Account.Role.ROLE_USER);
            account.setActive(true);
            accountRepository.save(account);

            // Tạo User
            User user = new User();
            user.setName("Test User - " + username);
            user.setEmail(username + "@test.com");
            user.setPhone("0987654321");
            user.setAccount(account);
            userRepository.save(user);

            return "✅ Test user created successfully!<br>" +
                   "Username: " + username + "<br>" +
                   "Password: " + password + "<br>" +
                   "<a href='/login'>Go to Login</a>";
        } catch (Exception e) {
            return "❌ Error: " + e.getMessage();
        }
    }
}

