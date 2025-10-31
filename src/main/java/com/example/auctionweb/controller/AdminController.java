package com.example.auctionweb.controller;

import com.example.auctionweb.entity.*;
import com.example.auctionweb.entity.AuctionRegistration.RegistrationStatus;
import com.example.auctionweb.entity.Product.ProductStatus;
import com.example.auctionweb.repository.*;
import com.example.auctionweb.service.IProductService;
import com.example.auctionweb.service.ProductService;
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

    @Autowired
    private IProductService productService;

    // Dashboard
    @GetMapping("")
    public String dashboardRoot(Model model) {
        model.addAttribute("pendingAuctions", registrationRepository.findByStatus(RegistrationStatus.PENDING).size());
        model.addAttribute("pendingProducts", productRepository.findByStatus(ProductStatus.PENDING).size());
        model.addAttribute("totalUsers", userRepository.findAll().size());
        return "admin/management";
    }

//    @GetMapping("/management")
//    public String dashboard(Model model) {
//        model.addAttribute("pendingAuctions", registrationRepository.findByStatus(RegistrationStatus.PENDING).size());
////        model.addAttribute("pendingProducts", productRepository.findByStatus(ProductStatus.PENDING).size());
//        model.addAttribute("totalUsers", userRepository.findAll().size());
//        return "admin/management";
//    }

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

    // ========== DASHBOARD ==========

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Product> allProducts = productService.getAllProductsForAdmin();
        List<Product> pendingProducts = productService.getProductsByStatus(Product.ProductStatus.PENDING);
        List<Category> categories = productService.getAllCategories();

        model.addAttribute("totalProducts", allProducts.size());
        model.addAttribute("pendingProducts", pendingProducts.size());
        model.addAttribute("totalCategories", categories.size());
        model.addAttribute("recentProducts", allProducts.stream().limit(5).toList());

        return "admin/dashboard";
    }

    // ========== CRUD SẢN PHẨM ==========

    // READ - Danh sách sản phẩm
    @GetMapping("/products")
    public String productManagement(
            @RequestParam(value = "status", required = false) Product.ProductStatus status,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            Model model) {

        List<Product> products;
        if (status != null && categoryId != null) {
            // Lọc theo cả status và category
            products = productService.getAllProductsForAdmin().stream()
                    .filter(p -> p.getStatus() == status)
                    .filter(p -> p.getCategory() != null && p.getCategory().getId().equals(categoryId))
                    .toList();
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
        model.addAttribute("statuses", Product.ProductStatus.values());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedCategoryId", categoryId);

        return "admin/products";
    }

    // READ - Chi tiết sản phẩm
    @GetMapping("/products/{id}")
    public String productDetail(@PathVariable Integer id, Model model) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return "redirect:/admin/products";
        }

        List<Category> categories = productService.getAllCategories();

        model.addAttribute("product", product);
        model.addAttribute("categories", categories);
        model.addAttribute("statuses", Product.ProductStatus.values());
        return "admin/product-detail";
    }

    // CREATE - Form thêm sản phẩm
    @GetMapping("/products/add")
    public String addProductForm(Model model) {
        List<Category> categories = productService.getAllCategories();
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categories);
        model.addAttribute("statuses", Product.ProductStatus.values());
        return "admin/product-form";
    }

    // CREATE - Thêm sản phẩm
    @PostMapping("/products")
    public String createProduct(
            @ModelAttribute Product product,
            @RequestParam Integer categoryId,
            RedirectAttributes redirectAttributes) {

        try {
            Category category = productService.getCategoryById(categoryId);
            product.setCategory(category);
            productService.saveProduct(product);
            redirectAttributes.addFlashAttribute("success", "Thêm sản phẩm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm sản phẩm: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }

    // UPDATE - Form chỉnh sửa sản phẩm
    @GetMapping("/products/{id}/edit")
    public String editProductForm(@PathVariable Integer id, Model model) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return "redirect:/admin/products";
        }

        List<Category> categories = productService.getAllCategories();

        model.addAttribute("product", product);
        model.addAttribute("categories", categories);
        model.addAttribute("statuses", Product.ProductStatus.values());
        return "admin/product-form";
    }

    // UPDATE - Cập nhật sản phẩm
    @PostMapping("/products/{id}")
    public String updateProduct(
            @PathVariable Integer id,
            @ModelAttribute Product productUpdate,
            @RequestParam Integer categoryId,
            RedirectAttributes redirectAttributes) {

        try {
            Product existingProduct = productService.getProductById(id);
            if (existingProduct == null) {
                redirectAttributes.addFlashAttribute("error", "Sản phẩm không tồn tại!");
                return "redirect:/admin/products";
            }

            Category category = productService.getCategoryById(categoryId);

            existingProduct.setName(productUpdate.getName());
            existingProduct.setDescription(productUpdate.getDescription());
            existingProduct.setStartingPrice(productUpdate.getStartingPrice());
            existingProduct.setImageUrl(productUpdate.getImageUrl());
            existingProduct.setCategory(category);
            existingProduct.setStatus(productUpdate.getStatus());

            productService.saveProduct(existingProduct);
            redirectAttributes.addFlashAttribute("success", "Cập nhật sản phẩm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật sản phẩm: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }

    // UPDATE - Cập nhật trạng thái sản phẩm
    @PostMapping("/products/{id}/status")
    public String updateProductStatus(
            @PathVariable Integer id,
            @RequestParam Product.ProductStatus status,
            RedirectAttributes redirectAttributes) {

        try {
            productService.updateProductStatus(id, status);
            redirectAttributes.addFlashAttribute("success", "Cập nhật trạng thái sản phẩm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật trạng thái: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }

    // DELETE - Xóa sản phẩm
    @PostMapping("/products/{id}/delete")
    public String deleteProduct(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {

        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("success", "Xóa sản phẩm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa sản phẩm: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }

    // ========== CRUD DANH MỤC ==========

    // READ - Danh sách danh mục
    @GetMapping("/categories")
    public String categoryManagement(Model model) {
        List<Category> categories = productService.getAllCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("category", new Category());
        return "admin/categories";
    }

    // READ - Chi tiết danh mục
    @GetMapping("/categories/{id}")
    public String categoryDetail(@PathVariable Integer id, Model model) {
        Category category = productService.getCategoryById(id);
        if (category == null) {
            return "redirect:/admin/categories";
        }

        List<Product> products = productService.getProductsByCategory(id);

        model.addAttribute("category", category);
        model.addAttribute("products", products);
        return "admin/category-detail";
    }

    // CREATE - Thêm danh mục
    @PostMapping("/categories")
    public String createCategory(
            @ModelAttribute Category category,
            RedirectAttributes redirectAttributes) {

        try {
            productService.createCategory(category);
            redirectAttributes.addFlashAttribute("success", "Tạo danh mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi tạo danh mục: " + e.getMessage());
        }

        return "redirect:/admin/categories";
    }

    // UPDATE - Cập nhật danh mục
    @PostMapping("/categories/{id}")
    public String updateCategory(
            @PathVariable Integer id,
            @RequestParam String name,
            RedirectAttributes redirectAttributes) {

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

    // DELETE - Xóa danh mục
    @PostMapping("/categories/{id}/delete")
    public String deleteCategory(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {

        try {
            productService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("success", "Xóa danh mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa danh mục: " + e.getMessage());
        }

        return "redirect:/admin/categories";
    }
}



