package com.example.auctionweb.controller;

import com.example.auctionweb.dto.ProductRequestDTO;
import com.example.auctionweb.entity.*;
import com.example.auctionweb.entity.AuctionRegistration.RegistrationStatus;
import com.example.auctionweb.entity.Product.ProductStatus;
import com.example.auctionweb.repository.*;
import com.example.auctionweb.service.IProductService;
import com.example.auctionweb.websocket.NotificationWebSocketHandler;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private AuctionRegistrationRepository registrationRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private AccountRepository accountRepository;
    @Autowired private NotificationRepository notificationRepository;
    @Autowired(required = false) private NotificationWebSocketHandler notificationWs;
    @Autowired private IProductService productService;

    // ================== ENTRY / DASHBOARD ==================

    @GetMapping({"", "/"})
    public String adminRoot() {
        return "redirect:/admin/dashboard";
    }

    @Autowired
    private com.example.auctionweb.service.implement.BidHistoryService bidHistoryService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Product> allProducts = productService.getAllProductsForAdmin();
        List<Product> pendingProducts = productService.getProductsByStatus(Product.ProductStatus.PENDING);

        model.addAttribute("totalProducts", allProducts.size());
        model.addAttribute("pendingProducts", pendingProducts.size());
        List<Product> recentProducts = allProducts.stream().limit(5).toList();
        model.addAttribute("recentProducts", recentProducts);
        List<Integer> productIds = recentProducts.stream()
                .map(Product::getId)
                .toList();
        Map<Integer, BigDecimal> highestBidMap =
                bidHistoryService.getHighestBidMapByProductIds(productIds);
        model.addAttribute("highestBidMap", highestBidMap);
        model.addAttribute("totalUsers", userRepository.findAll().size());
        model.addAttribute("totalCategories", productService.getAllCategories().size());
        int pendingRegs = registrationRepository.findByStatus(AuctionRegistration.RegistrationStatus.PENDING).size();
        model.addAttribute("pendingRegistrations", pendingRegs);
        model.addAttribute("totalAuctions", registrationRepository.findAll().size());

        return "admin/dashboard";
    }


    // ================== AUCTION REGISTRATIONS ==================

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

    @PostMapping("/auction-registrations/{id}/approve") public String approveAuctionRegistration(@PathVariable("id") Integer id) throws Exception { AuctionRegistration reg = registrationRepository.findById(id).orElse(null); if (reg != null) { reg.setStatus(RegistrationStatus.APPROVED); registrationRepository.save(reg); Notification n = new Notification(); n.setUser(reg.getUser()); n.setNotification("Đăng ký tham gia đấu giá đã được duyệt"); n.setTime(LocalDateTime.now()); notificationRepository.save(n); notificationWs.broadcastNotification(n); } return "redirect:/admin/auction-registrations"; } @PostMapping("/auction-registrations/{id}/reject") public String rejectAuctionRegistration(@PathVariable("id") Integer id) throws Exception { AuctionRegistration reg = registrationRepository.findById(id).orElse(null); if (reg != null) { reg.setStatus(RegistrationStatus.REJECTED); registrationRepository.save(reg); Notification n = new Notification(); n.setUser(reg.getUser()); n.setNotification("Đăng ký tham gia đấu giá đã bị từ chối"); n.setTime(LocalDateTime.now()); notificationRepository.save(n); notificationWs.broadcastNotification(n); } return "redirect:/admin/auction-registrations"; }

    // ================== PRODUCT REGISTRATIONS ==================

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

    @PostMapping("/product-registrations/{id}/approve") public String approveProduct(@PathVariable("id") Integer id) throws Exception { Product p = productRepository.findById(id).orElse(null); if (p != null) { p.setStatus(ProductStatus.APPROVED); productRepository.save(p); Notification n = new Notification(); n.setUser(p.getSeller()); n.setNotification("Sản phẩm của bạn đã được duyệt"); n.setTime(LocalDateTime.now()); notificationRepository.save(n); notificationWs.broadcastNotification(n); } return "redirect:/admin/product-registrations"; } @PostMapping("/product-registrations/{id}/reject") public String rejectProduct(@PathVariable("id") Integer id) throws Exception { Product p = productRepository.findById(id).orElse(null); if (p != null) { p.setStatus(ProductStatus.REJECTED); productRepository.save(p); Notification n = new Notification(); n.setUser(p.getSeller()); n.setNotification("Sản phẩm của bạn đã bị từ chối"); n.setTime(LocalDateTime.now()); notificationRepository.save(n); notificationWs.broadcastNotification(n); } return "redirect:/admin/product-registrations"; }

    // ================== USERS ==================

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

    // ================== ACCOUNTS (fallback) ==================

    @GetMapping("/accounts")
    public String listAccounts(Model model) {
        List<Account> accounts = accountRepository.findAll();
        model.addAttribute("accounts", accounts);
        return "admin/users";
    }

    // ================== PRODUCTS (CRUD với DTO) ==================

    // LIST
    @GetMapping("/products")
    public String productManagement(
            @RequestParam(value = "status", required = false) ProductStatus status,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            Model model) {

        List<Product> products;
        if (status != null && categoryId != null) {
            products = productService.getAllProductsForAdmin().stream()
                    .filter(p -> p.getStatus() == status)
                    .filter(p -> p.getCategory() != null && p.getCategory().getId().equals(categoryId))
                    .collect(Collectors.toList());
        } else if (status != null) {
            products = productService.getProductsByStatus(status);
        } else if (categoryId != null) {
            products = productService.getProductsByCategory(categoryId);
        } else {
            products = productService.getAllProductsForAdmin();
        }

        List<Category> categories = productService.getAllCategories();

        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("statuses", ProductStatus.values());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedCategoryId", categoryId);

        return "admin/products";
    }

    // DETAIL (giữ nguyên – có thể dùng entity để xem)
    @GetMapping("/products/{id}")
    public String productDetail(@PathVariable Integer id, Model model) {
        Product product = productService.getProductById(id);
        if (product == null) return "redirect:/admin/products";

        List<Category> categories = productService.getAllCategories();
        model.addAttribute("productEntity", product); // nếu muốn hiển thị read-only
        model.addAttribute("categories", categories);
        model.addAttribute("statuses", ProductStatus.values());
        return "admin/product-detail";
    }

    // ADD FORM (bind vào DTO)
    @GetMapping("/products/add")
    public String addProductForm(Model model) {
        List<Category> categories = productService.getAllCategories();
        model.addAttribute("product", new ProductRequestDTO()); // DTO
        model.addAttribute("categories", categories);
        // Nếu form không cần status (DTO không có), bỏ statuses
        return "admin/product-form";
    }

    // CREATE (nhận DTO)
    @PostMapping("/products")
    public String createProduct(
            @Valid @ModelAttribute("product") ProductRequestDTO productDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        // validate highestPrice >= startingPrice (nếu có trong DTO)
        if (!validateHighestPrice(productDTO.getStartingPrice(), productDTO.getHighestPrice())) {
            bindingResult.rejectValue("highestPrice", "price.invalid", "Giá cao nhất không được nhỏ hơn giá khởi điểm!");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", productService.getAllCategories());
            // Nếu template đang hiển thị statuses nhưng DTO không có -> đừng add statuses
            return "admin/product-form";
        }

        try {
            productService.createProduct(productDTO);
            redirectAttributes.addFlashAttribute("success", "Thêm sản phẩm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm sản phẩm: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    // EDIT FORM (map entity -> DTO để bind form)
    @GetMapping("/products/{id}/edit")
    public String editProductForm(@PathVariable Integer id, Model model) {
        Product p = productService.getProductById(id);
        if (p == null) return "redirect:/admin/products";

        ProductRequestDTO dto = new ProductRequestDTO();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setDescription(p.getDescription());
        dto.setStartingPrice(p.getStartingPrice());
        dto.setImageUrl(p.getImageUrl());
        dto.setCategoryId(p.getCategory() != null ? p.getCategory().getId() : null);

        BigDecimal highestBid = bidHistoryService.getHighestBidByProductId(id);
        model.addAttribute("highestBid", highestBid);

        model.addAttribute("product", dto);
        model.addAttribute("categories", productService.getAllCategories());
        return "admin/product-form";
    }


    // UPDATE (nhận DTO)
    @PostMapping("/products/{id}")
    public String updateProduct(
            @PathVariable Integer id,
            @Valid @ModelAttribute("product") ProductRequestDTO productDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (!validateHighestPrice(productDTO.getStartingPrice(), productDTO.getHighestPrice())) {
            bindingResult.rejectValue("highestPrice", "price.invalid", "Giá cao nhất không được nhỏ hơn giá khởi điểm!");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", productService.getAllCategories());
            return "admin/product-form";
        }

        try {
            productService.updateProduct(id, productDTO);
            redirectAttributes.addFlashAttribute("success", "Cập nhật sản phẩm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật sản phẩm: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }

    // QUICK UPDATE STATUS (giữ nguyên – dùng entity bên service)
    @PostMapping("/products/{id}/status")
    public String updateProductStatus(
            @PathVariable Integer id,
            @RequestParam ProductStatus status,
            RedirectAttributes redirectAttributes) {

        try {
            productService.updateProductStatus(id, status);
            redirectAttributes.addFlashAttribute("success", "Cập nhật trạng thái sản phẩm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật trạng thái: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    // DELETE
    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("success", "Xóa sản phẩm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa sản phẩm: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    // ================== CATEGORIES (CRUD) ==================

    @GetMapping("/categories")
    public String categoryManagement(Model model) {
        List<Category> categories = productService.getAllCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("category", new Category());
        return "admin/categories";
    }

    @GetMapping("/categories/{id}")
    public String categoryDetail(@PathVariable Integer id, Model model) {
        Category category = productService.getCategoryById(id);
        if (category == null) return "redirect:/admin/categories";

        List<Product> products = productService.getProductsByCategory(id);
        model.addAttribute("category", category);
        model.addAttribute("products", products);
        return "admin/category-detail";
    }

    @PostMapping("/categories")
    public String createCategory(@ModelAttribute Category category, RedirectAttributes redirectAttributes) {
        try {
            productService.createCategory(category);
            redirectAttributes.addFlashAttribute("success", "Tạo danh mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi tạo danh mục: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/{id}")
    public String updateCategory(@PathVariable Integer id, @RequestParam String name, RedirectAttributes redirectAttributes) {
        try {
            Category category = new Category();
            category.setName(name);
            productService.updateCategory(id, category);
            redirectAttributes.addFlashAttribute("success", "Cập nhật danh mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật danh mục: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/{id}/delete")
    public String deleteCategory(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("success", "Xóa danh mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa danh mục: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    // ================== HELPERS ==================

    private boolean validateHighestPrice(BigDecimal startingPrice, BigDecimal highestPrice) {
        if (startingPrice == null || highestPrice == null) return true; // bỏ qua nếu chưa nhập
        return highestPrice.compareTo(startingPrice) >= 0;
    }

    @GetMapping("/__ping")
    @ResponseBody
    public String ping() { return "ok"; }
}
