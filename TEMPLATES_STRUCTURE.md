# 📁 CẤU TRÚC TEMPLATES

## ✅ Cấu trúc đã được tổ chức lại

```
src/main/resources/templates/
│
├── 📂 admin/                         ← ADMIN PANEL
│   ├── management.html               → /admin hoặc /admin/management
│   ├── auction-registrations.html    → /admin/auction-registrations
│   ├── product-registrations.html    → /admin/product-registrations
│   └── users.html                    → /admin/users
│
├── 📂 auth/                          ← AUTHENTICATION
│   ├── signin.html                   → /signin hoặc /login
│   └── signup.html                   → /signup hoặc /register
│
├── 📂 user/                          ← USER PROFILE & SETTINGS
│   ├── profile.html                  → /user/profile
│   ├── change-password.html          → /user/change-password
│   └── information.html              → /user/information
│
├── 📂 auction/                       ← AUCTION PAGES
│   └── auction.html                  → /auction/{id} hoặc /auctions
│
├── 📂 product/                       ← PRODUCT PAGES
│   ├── goods.html                    → /products hoặc /goods
│   └── search.html                   → /search hoặc /products/search
│
├── 📂 static-pages/                  ← STATIC CONTENT
│   └── contact.html                  → /contact hoặc /lien-he
│
├── 📂 layout/                        ← LAYOUT TEMPLATES
│   └── layout.html                   → Thymeleaf layout template
│
├── 📂 users/                         ← (Empty folder - có thể xóa)
│
├── home.html                         → / hoặc /home (Main homepage)
└── home1.html                        → /home1 (Alternative homepage)

```

## 📝 CONTROLLER MAPPING CẦN CẬP NHẬT

### 1. AuthController (Cần tạo hoặc cập nhật)
```java
@Controller
public class AuthController {
    
    @GetMapping("/signin")
    public String signin() {
        return "auth/signin";  // ← Thay vì "Signin"
    }
    
    @GetMapping("/signup")
    public String signup() {
        return "auth/signup";  // ← Thay vì "Signup"
    }
}
```

### 2. UserController (Cần cập nhật)
```java
@Controller
@RequestMapping("/user")
public class UserController {
    
    @GetMapping("/profile")
    public String profile() {
        return "user/profile";  // ← Thay vì "User"
    }
    
    @GetMapping("/change-password")
    public String changePassword() {
        return "user/change-password";  // ← Thay vì "Changepw"
    }
    
    @GetMapping("/information")
    public String information() {
        return "user/information";  // ← Thay vì "Information"
    }
}
```

### 3. AuctionController (Cần cập nhật)
```java
@Controller
@RequestMapping("/auction")
public class AuctionController {
    
    @GetMapping("/{id}")
    public String auctionDetail(@PathVariable Integer id, Model model) {
        return "auction/auction";  // ← Thay vì "auction"
    }
}
```

### 4. ProductController (Cần cập nhật)
```java
@Controller
public class ProductController {
    
    @GetMapping("/products")
    public String products() {
        return "product/goods";  // ← Thay vì "Goods"
    }
    
    @GetMapping("/search")
    public String search() {
        return "product/search";  // ← Thay vì "Search"
    }
}
```

### 5. StaticPagesController (Cần tạo)
```java
@Controller
public class StaticPagesController {
    
    @GetMapping("/contact")
    public String contact() {
        return "static-pages/contact";  // ← Thay vì "Lienhe"
    }
}
```

### 6. HomeController (Đã có - không cần thay đổi)
```java
@Controller
public class HomeController {
    
    @GetMapping("/home")
    public String home() {
        return "home1";  // Giữ nguyên
    }
}
```

## 🔄 CÁCH CẬP NHẬT CONTROLLERS

### Bước 1: Tìm tất cả return statements
```bash
# Tìm các controller đang dùng tên cũ
grep -r "return \"Signin\"" src/main/java/
grep -r "return \"Signup\"" src/main/java/
grep -r "return \"User\"" src/main/java/
grep -r "return \"Changepw\"" src/main/java/
grep -r "return \"Information\"" src/main/java/
grep -r "return \"Goods\"" src/main/java/
grep -r "return \"Search\"" src/main/java/
grep -r "return \"Lienhe\"" src/main/java/
```

### Bước 2: Thay thế theo mapping mới
| Tên cũ | Tên mới |
|--------|---------|
| `Signin` | `auth/signin` |
| `Signup` | `auth/signup` |
| `User` | `user/profile` |
| `Changepw` | `user/change-password` |
| `Information` | `user/information` |
| `auction` | `auction/auction` |
| `Goods` | `product/goods` |
| `Search` | `product/search` |
| `Lienhe` | `static-pages/contact` |

## 🎯 LỢI ÍCH CỦA CẤU TRÚC MỚI

✅ **Tổ chức rõ ràng** - Dễ tìm file theo chức năng  
✅ **Scalability** - Dễ mở rộng thêm pages mới  
✅ **Team collaboration** - Dev khác dễ hiểu cấu trúc  
✅ **Maintainability** - Dễ bảo trì và refactor  
✅ **Convention** - Theo chuẩn Spring Boot best practices  

## 📋 CHECKLIST

- [x] Tạo các thư mục con (auth, user, auction, product, static-pages)
- [x] Di chuyển các file HTML vào đúng package
- [x] Đổi tên file theo convention (lowercase, kebab-case)
- [ ] Cập nhật tất cả controllers để return đúng path
- [ ] Test tất cả các routes
- [ ] Xóa folder `users/` nếu không dùng
- [ ] Cập nhật documentation

## 🚨 LƯU Ý

⚠️ **Sau khi di chuyển file, PHẢI cập nhật controllers!**  
⚠️ **Kiểm tra lại tất cả các route để đảm bảo không bị 404**  
⚠️ **Nếu có Thymeleaf fragments, cập nhật path references**  

## 🔍 KIỂM TRA SAU KHI CẬP NHẬT

```bash
# 1. Compile để check lỗi
./gradlew compileJava

# 2. Chạy ứng dụng
./gradlew bootRun

# 3. Test các routes:
- http://localhost:8080/
- http://localhost:8080/signin
- http://localhost:8080/signup
- http://localhost:8080/user/profile
- http://localhost:8080/products
- http://localhost:8080/search
- http://localhost:8080/contact
- http://localhost:8080/admin
```

---

**Ngày cập nhật:** 2025-10-29  
**Trạng thái:** ✅ Cấu trúc đã được tổ chức lại - Cần cập nhật Controllers


