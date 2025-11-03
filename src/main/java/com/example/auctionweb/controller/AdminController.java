package com.example.auctionweb.controller;

import com.example.auctionweb.dto.AuctionDto;
import com.example.auctionweb.dto.ProductRequestDTO;
import com.example.auctionweb.entity.*;
import com.example.auctionweb.entity.AuctionRegistration.RegistrationStatus;
import com.example.auctionweb.entity.Product.ProductStatus;
import com.example.auctionweb.repository.*;
import com.example.auctionweb.service.IProductService;
import com.example.auctionweb.websocket.NotificationWebSocketHandler;
import jakarta.validation.Valid;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    // ===== Injects =====
    @Autowired private IProductService productService;
    @Autowired private ProductRepository productRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private AccountRepository accountRepository;
    @Autowired private NotificationRepository notificationRepository;
    @Autowired private AuctionRegistrationRepository registrationRepository;
    @Autowired(required = false) private NotificationWebSocketHandler notificationWs;
    @Autowired private BidHistoryRepository bidHistoryRepository;
    @Autowired private AuctionRegistrationRepository  auctionRegistrationRepository;



    @Autowired(required = false) private AuctionRepository auctionRepository;

    // Bid history service bạn đang dùng
    @Autowired private com.example.auctionweb.service.implement.BidHistoryService bidHistoryService;

    // ===== Trim String inputs ("" -> null) =====
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    // ===== Entry =====
    @GetMapping({"", "/"})
    public String adminRoot() { return "redirect:/admin/dashboard"; }

    // ===== Dashboard =====
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Product> allProducts = productService.getAllProductsForAdmin();
        List<Product> pendingProducts = productService.getProductsByStatus(ProductStatus.PENDING);

        model.addAttribute("totalProducts", allProducts.size());
        model.addAttribute("pendingProducts", pendingProducts.size());

        List<Product> recentProducts = allProducts.stream().limit(5).toList();
        model.addAttribute("recentProducts", recentProducts);

        List<Integer> productIds = recentProducts.stream().map(Product::getId).toList();
        Map<Integer, BigDecimal> highestBidMap =
                bidHistoryService.getHighestBidMapByProductIds(productIds);
        model.addAttribute("highestBidMap", highestBidMap);

        model.addAttribute("totalUsers", userRepository.findAll().size());
        model.addAttribute("totalCategories", productService.getAllCategories().size());
        int pendingRegs = registrationRepository.findByStatus(RegistrationStatus.PENDING).size();
        model.addAttribute("pendingRegistrations", pendingRegs);
        model.addAttribute("totalAuctions", registrationRepository.findAll().size());

        return "admin/dashboard";
    }

    // ===== Auction Registrations =====
    @GetMapping("/auction-registrations")
    public String listAuctionRegistrations(@RequestParam(defaultValue = "PENDING") String status, Model model) {
        List<AuctionRegistration> registrations =
                "ALL".equalsIgnoreCase(status)
                        ? registrationRepository.findAll()
                        : registrationRepository.findByStatus(RegistrationStatus.valueOf(status.toUpperCase()));
        model.addAttribute("registrations", registrations);
        model.addAttribute("currentStatus", status.toUpperCase());
        return "admin/auction-registrations";
    }

    @PostMapping("/auction-registrations/{id}/approve")
    public String approveAuctionRegistration(@PathVariable Integer id) {
        AuctionRegistration reg = registrationRepository.findById(id).orElse(null);
        if (reg != null) {
            reg.setStatus(RegistrationStatus.APPROVED);
            registrationRepository.save(reg);

            Notification n = new Notification();
            n.setUser(reg.getUser());
            n.setNotification("Đăng ký tham gia đấu giá đã được duyệt");
            n.setTime(LocalDateTime.now());
            notificationRepository.save(n);

            if (notificationWs != null) {
                try { notificationWs.broadcastNotification(n); } catch (Exception ignore) {}
            }
        }
        return "redirect:/admin/auction-registrations";
    }

    @PostMapping("/auction-registrations/{id}/reject")
    public String rejectAuctionRegistration(@PathVariable Integer id) {
        AuctionRegistration reg = registrationRepository.findById(id).orElse(null);
        if (reg != null) {
            reg.setStatus(RegistrationStatus.REJECTED);
            registrationRepository.save(reg);

            Notification n = new Notification();
            n.setUser(reg.getUser());
            n.setNotification("Đăng ký tham gia đấu giá đã bị từ chối");
            n.setTime(LocalDateTime.now());
            notificationRepository.save(n);

            if (notificationWs != null) {
                try { notificationWs.broadcastNotification(n); } catch (Exception ignore) {}
            }
        }
        return "redirect:/admin/auction-registrations";
    }

    // ===== Product Registrations =====
    @GetMapping("/product-registrations")
    public String listProductRegistrations(@RequestParam(defaultValue = "PENDING") String status, Model model) {
        List<Product> products =
                "ALL".equalsIgnoreCase(status)
                        ? productRepository.findAll()
                        : productRepository.findByStatus(ProductStatus.valueOf(status.toUpperCase()));
        model.addAttribute("products", products);
        model.addAttribute("currentStatus", status.toUpperCase());
        return "admin/product-registrations";
    }

    @PostMapping("/product-registrations/{id}/approve")
    public String approveProduct(@PathVariable Integer id) {
        Product p = productRepository.findById(id).orElse(null);
        if (p != null) {
            p.setStatus(ProductStatus.APPROVED);
            productRepository.save(p);

            Notification n = new Notification();
            n.setUser(p.getSeller());
            n.setNotification("Sản phẩm của bạn đã được duyệt");
            n.setTime(LocalDateTime.now());
            notificationRepository.save(n);

            if (notificationWs != null) {
                try { notificationWs.broadcastNotification(n); } catch (Exception ignore) {}
            }
        }
        return "redirect:/admin/product-registrations";
    }

    @PostMapping("/product-registrations/{id}/reject")
    public String rejectProduct(@PathVariable Integer id) {
        Product p = productRepository.findById(id).orElse(null);
        if (p != null) {
            p.setStatus(ProductStatus.REJECTED);
            productRepository.save(p);

            Notification n = new Notification();
            n.setUser(p.getSeller());
            n.setNotification("Sản phẩm của bạn đã bị từ chối");
            n.setTime(LocalDateTime.now());
            notificationRepository.save(n);

            if (notificationWs != null) {
                try { notificationWs.broadcastNotification(n); } catch (Exception ignore) {}
            }
        }
        return "redirect:/admin/product-registrations";
    }

    // ===== Users & Accounts =====
    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
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

    @GetMapping("/accounts")
    public String listAccounts(Model model) {
        model.addAttribute("accounts", accountRepository.findAll());
        return "admin/users";
    }

    // ===== Products (CRUD via DTO) =====
    @GetMapping("/products")
    public String productManagement(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "status", required = false) ProductStatus status,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            Model model) {

        List<Product> products = productService.getAllProductsForAdmin();

        if (status != null) {
            products = products.stream()
                    .filter(p -> p.getStatus() == status)
                    .collect(Collectors.toList());
        }
        if (categoryId != null) {
            products = products.stream()
                    .filter(p -> p.getCategory() != null && Objects.equals(p.getCategory().getId(), categoryId))
                    .collect(Collectors.toList());
        }
        if (q != null && !q.isBlank()) {
            String key = q.trim().toLowerCase();
            products = products.stream()
                    .filter(p -> p.getName() != null && p.getName().toLowerCase().contains(key))
                    .collect(Collectors.toList());
        }

        model.addAttribute("products", products);
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("statuses", ProductStatus.values());
        model.addAttribute("q", q);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedCategoryId", categoryId);

        return "admin/products";
    }

    @GetMapping("/products/{id}")
    public String productDetail(@PathVariable Integer id, Model model) {
        Product product = productService.getProductById(id);
        if (product == null) return "redirect:/admin/products";

        model.addAttribute("productEntity", product);
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("statuses", ProductStatus.values());
        return "admin/product-detail";
    }

    // Add form
    @GetMapping("/products/add")
    public String addProductForm(Model model) {
        model.addAttribute("product", new ProductRequestDTO());
        model.addAttribute("categories", productService.getAllCategories());
        return "admin/product-add";
    }

    // Create (highestPrice chỉ dùng form, không lưu DB)
    @PostMapping("/products")
    public String createProduct(@Valid @ModelAttribute("product") ProductRequestDTO productDTO,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {

        if (!validateHighestPrice(productDTO.getStartingPrice(), productDTO.getHighestPrice())) {
            bindingResult.rejectValue("highestPrice", "price.invalid",
                    "Giá cao nhất không được nhỏ hơn giá khởi điểm!");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", productService.getAllCategories());
            return "admin/product-add";
        }

        try {
            productService.createProduct(productDTO); // Service của bạn không map highestPrice xuống entity
            redirectAttributes.addFlashAttribute("success", "Thêm sản phẩm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm sản phẩm: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    // Edit form (entity -> DTO)
    @GetMapping("/products/{id}/edit")
    public String editProductForm(@PathVariable Integer id, Model model) {
        Product p = productService.getProductById(id);
        if (p == null) return "redirect:/admin/products";

        ProductRequestDTO dto = new ProductRequestDTO();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setDescription(p.getDescription());
        dto.setStartingPrice(p.getStartingPrice());
        dto.setCategoryId(p.getCategory() != null ? p.getCategory().getId() : null);
        dto.setImageUrl(p.getImageUrl()); // tên file trong static/images

        BigDecimal highestBid = bidHistoryService.getHighestBidByProductId(id);
        dto.setHighestPrice(highestBid);

        model.addAttribute("highestBid", highestBid);
        model.addAttribute("product", dto);
        model.addAttribute("categories", productService.getAllCategories());
        return "admin/product-form";
    }

    // Update
    @PostMapping("/products/{id}")
    public String updateProduct(@PathVariable Integer id,
                                @Valid @ModelAttribute("product") ProductRequestDTO productDTO,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {

        if (!validateHighestPrice(productDTO.getStartingPrice(), productDTO.getHighestPrice())) {
            bindingResult.rejectValue("highestPrice", "price.invalid",
                    "Giá cao nhất không được nhỏ hơn giá khởi điểm!");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", productService.getAllCategories());
            return "admin/product-form";
        }

        try {
            productService.updateProduct(id, productDTO); // KHÔNG map highestPrice xuống entity
            redirectAttributes.addFlashAttribute("success", "Cập nhật sản phẩm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật sản phẩm: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    // Quick update status
    @PostMapping("/products/{id}/status")
    public String updateProductStatus(@PathVariable Integer id,
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

    // Delete
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

    // ===== Categories =====
    @GetMapping("/categories")
    public String categoryManagement(Model model) {
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("category", new Category());
        return "admin/categories";
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
    public String updateCategory(@PathVariable Integer id,
                                 @RequestParam String name,
                                 RedirectAttributes redirectAttributes) {
        try {
            Category c = new Category();
            c.setName(name);
            productService.updateCategory(id, c);
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

    @GetMapping("/auctions")
    public String auctions(Model model) {
        List<Auction> auctions = auctionRepository.findAll();
        if (auctions == null || auctions.isEmpty()) {
            model.addAttribute("auctions", Collections.emptyList());
            model.addAttribute("highestMap", Collections.emptyMap());
            model.addAttribute("bidCountMap", Collections.emptyMap());
            model.addAttribute("participantMap", Collections.emptyMap());
            model.addAttribute("statusMap", Collections.emptyMap());
            return "admin/auctions";
        }

        List<Integer> auctionIds = auctions.stream()
                .map(Auction::getId)
                .filter(Objects::nonNull)
                .toList();
        Map<Integer, BigDecimal> highestMap =
                bidHistoryService.getHighestBidMapByAuctionIds(auctionIds);
        Map<Integer, Long> bidCountMap =
                bidHistoryService.getBidCountMapByAuctionIds(auctionIds);
        Map<Integer, Long> participantMap = auctions.stream()
                .collect(Collectors.toMap(
                        Auction::getId,
                        a -> auctionRegistrationRepository.countRegsByAuction(a.getId())
                ));
        Map<Integer, String> statusMap = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        for (Auction a : auctions) {
            String stt;
            if (a.getStartTime() != null && now.isBefore(a.getStartTime())) {
                stt = "CHƯA BẮT ĐẦU";
            } else if (a.getEndTime() != null && now.isAfter(a.getEndTime())) {
                stt = "KẾT THÚC";
            } else {
                stt = "ĐANG DIỄN RA";
            }
            statusMap.put(a.getId(), stt);
        }

        model.addAttribute("auctions", auctions);
        model.addAttribute("highestMap", highestMap);
        model.addAttribute("bidCountMap", bidCountMap);
        model.addAttribute("participantMap", participantMap);
        model.addAttribute("statusMap", statusMap);
        return "admin/auctions";
    }


    @GetMapping("/auctions/{id}")
    public String auctionDetail(@PathVariable Integer id, Model model) {
        var auctionOpt = auctionRepository.findById(id);
        if (auctionOpt.isEmpty()) return "redirect:/admin/auctions";
        Auction auction = auctionOpt.get();

        // dữ liệu gốc
        var histories = bidHistoryService.findByAuctionId(id);
        var highest = bidHistoryService.getHighestBidByAuctionId(id);
        long participants = auctionRegistrationRepository.countByAuction_Id(id);

        // Map -> BidDTO (dùng User.getName() theo entity bạn đưa)
        var fmt = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        java.util.List<com.example.auctionweb.dto.BidDTO> bidDTOs = histories.stream().map(h -> {
            String display = "—";
            try {
                if (h.getUser() != null) {
                    var u = h.getUser();
                    if (u.getName() != null && !u.getName().isBlank()) display = u.getName();
                    else if (u.getEmail() != null && !u.getEmail().isBlank()) display = u.getEmail();
                }
            } catch (Throwable ignore) {}

            String timeText = (h.getTime() != null) ? fmt.format(h.getTime()) : "—";
            return new com.example.auctionweb.dto.BidDTO(display, h.getAmount(), timeText);
        }).toList();

        model.addAttribute("auction", auction);
        model.addAttribute("highest", highest);
        model.addAttribute("participants", participants);
        model.addAttribute("bidDTOs", bidDTOs);
        return "admin/auction-detail";
    }

    private boolean validateHighestPrice(BigDecimal startingPrice, BigDecimal highestPrice) {
        if (startingPrice == null || highestPrice == null) return true;
        return highestPrice.compareTo(startingPrice) >= 0;
    }

    @GetMapping("/__ping")
    @ResponseBody
    public String ping() { return "ok"; }
}
