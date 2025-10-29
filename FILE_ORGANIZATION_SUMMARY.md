# 📋 TỔ CHỨC LẠI CẤU TRÚC FILE - HOÀN TẤT

## ✅ ĐÃ THỰC HIỆN

### 📁 Cấu trúc Templates mới

```
src/main/resources/templates/
│
├── 📂 admin/                         ✅ Đã có - Không cần thay đổi
│   ├── management.html
│   ├── auction-registrations.html
│   ├── product-registrations.html
│   └── users.html
│
├── 📂 auth/                          ✅ MỚI TẠO
│   ├── signin.html                   (từ Signin.html)
│   └── signup.html                   (từ Signup.html)
│
├── 📂 user/                          ✅ MỚI TẠO
│   ├── profile.html                  (từ User.html)
│   ├── change-password.html          (từ Changepw.html)
│   └── information.html              (từ Information.html)
│
├── 📂 auction/                       ✅ MỚI TẠO
│   └── auction.html                  (từ auction.html)
│
├── 📂 product/                       ✅ Đã có folder
│   ├── goods.html                    (từ Goods.html)
│   └── search.html                   (từ Search.html)
│
├── 📂 static-pages/                  ✅ MỚI TẠO
│   └── contact.html                  (từ Lienhe.html)
│
├── 📂 layout/                        ✅ Đã có - Không đổi
│   └── layout.html
│
├── 📂 users/                         ⚠️ EMPTY - Có thể xóa
│
├── home.html                         ✅ Giữ nguyên
└── home1.html                        ✅ Giữ nguyên (đang dùng)
```

## 📊 BẢNG MAPPING FILE

| File cũ | File mới | Trạng thái |
|---------|----------|-----------|
| `Signin.html` | `auth/signin.html` | ✅ Đã di chuyển |
| `Signup.html` | `auth/signup.html` | ✅ Đã di chuyển |
| `User.html` | `user/profile.html` | ✅ Đã di chuyển |
| `Changepw.html` | `user/change-password.html` | ✅ Đã di chuyển |
| `Information.html` | `user/information.html` | ✅ Đã di chuyển |
| `auction.html` | `auction/auction.html` | ✅ Đã di chuyển |
| `Goods.html` | `product/goods.html` | ✅ Đã di chuyển |
| `Search.html` | `product/search.html` | ✅ Đã di chuyển |
| `Lienhe.html` | `static-pages/contact.html` | ✅ Đã di chuyển |
| `home.html` | `home.html` | ✅ Giữ nguyên |
| `home1.html` | `home1.html` | ✅ Giữ nguyên |

## 🎯 CONTROLLERS HIỆN TẠI

### ✅ Controllers đã có (không cần thay đổi)

1. **AdminController.java**
   - ✅ Đã dùng đúng path: `admin/management`, `admin/users`, etc.
   
2. **HomeController.java**
   - ✅ Đã dùng đúng path: `home1`

3. **ListBidController.java**
   - ✅ Không liên quan đến templates đã di chuyển

## 📝 CẦN TẠO/CẬP NHẬT CONTROLLERS

### 1. AuthController - CẦN TẠO MỚI

```java
package com.example.auctionweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {
    
    @GetMapping("/signin")
    public String signin() {
        return "auth/signin";
    }
    
    @GetMapping("/login")
    public String login() {
        return "auth/signin";
    }
    
    @GetMapping("/signup")
    public String signup() {
        return "auth/signup";
    }
    
    @GetMapping("/register")
    public String register() {
        return "auth/signup";
    }
}
```

### 2. UserController - CẦN TẠO MỚI

```java
package com.example.auctionweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {
    
    @GetMapping("/profile")
    public String profile() {
        return "user/profile";
    }
    
    @GetMapping("/change-password")
    public String changePassword() {
        return "user/change-password";
    }
    
    @GetMapping("/information")
    public String information() {
        return "user/information";
    }
}
```

### 3. AuctionController - CẦN TẠO MỚI

```java
package com.example.auctionweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auction")
public class AuctionController {
    
    @GetMapping("/{id}")
    public String auctionDetail(@PathVariable Integer id, Model model) {
        // TODO: Load auction details
        model.addAttribute("auctionId", id);
        return "auction/auction";
    }
    
    @GetMapping("/list")
    public String auctionList() {
        return "auction/auction";
    }
}
```

### 4. ProductController - CẦN TẠO MỚI

```java
package com.example.auctionweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProductController {
    
    @GetMapping("/products")
    public String products() {
        return "product/goods";
    }
    
    @GetMapping("/goods")
    public String goods() {
        return "product/goods";
    }
    
    @GetMapping("/search")
    public String search() {
        return "product/search";
    }
}
```

### 5. StaticPagesController - CẦN TẠO MỚI

```java
package com.example.auctionweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StaticPagesController {
    
    @GetMapping("/contact")
    public String contact() {
        return "static-pages/contact";
    }
    
    @GetMapping("/lien-he")
    public String lienHe() {
        return "static-pages/contact";
    }
}
```

## 🚀 CÁC BƯỚC TIẾP THEO

### Bước 1: Tạo các Controllers mới
```bash
# Tạo file AuthController.java
# Tạo file UserController.java
# Tạo file AuctionController.java
# Tạo file ProductController.java
# Tạo file StaticPagesController.java
```

### Bước 2: Test các routes
```bash
# Khởi động ứng dụng
./gradlew bootRun

# Test các URLs:
http://localhost:8080/signin
http://localhost:8080/signup
http://localhost:8080/user/profile
http://localhost:8080/user/change-password
http://localhost:8080/products
http://localhost:8080/search
http://localhost:8080/contact
http://localhost:8080/auction/1
```

### Bước 3: Dọn dẹp
```bash
# Xóa folder trống
Remove-Item "src\main\resources\templates\users\" -Force
```

## 📌 LƯU Ý QUAN TRỌNG

⚠️ **Sau khi tạo controllers:**
1. Kiểm tra lại tất cả các routes
2. Cập nhật các links trong HTML nếu cần
3. Test đăng nhập/đăng ký
4. Test navigation giữa các trang

⚠️ **Convention:**
- Folder name: lowercase, kebab-case
- File name: lowercase, kebab-case
- Controller mapping: theo RESTful convention
- Template path: `folder/file-name`

## 🎯 LỢI ÍCH

✅ **Tổ chức rõ ràng** - Easy to navigate  
✅ **Scalable** - Easy to add new pages  
✅ **Maintainable** - Easy to refactor  
✅ **Professional** - Follows best practices  
✅ **Team-friendly** - Clear structure for collaboration  

---

**Status:** ✅ File organization completed - Controllers need to be created  
**Date:** 2025-10-29

