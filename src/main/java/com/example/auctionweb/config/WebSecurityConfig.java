package com.example.auctionweb.config;

import com.example.auctionweb.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class WebSecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    //    SpringSecurityDialect là một thành phần tích hợp giữa Spring Security và Thymeleaf,
    //    giúp bạn dễ dàng kiểm soát và hiển thị nội dung trong các template Thymeleaf dựa trên quyền hạn và trạng thái xác thực của người dùng.
    //    Dưới đây là giải thích chi tiết về mục đích và cách sử dụng SpringSecurityDialect trong ứng dụng Spring Boot của bạn.
    @Bean
    public SpringSecurityDialect springSecurityDialect() {
        return new SpringSecurityDialect();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
//        String rawPassword = "123";
//        String encodedPassword = encoder.encode(rawPassword);
//
//        System.out.println(encodedPassword);
        return bCryptPasswordEncoder;
    }
    // xác thực
    @Bean
    public AuthenticationProvider authenticationProvider() {
        // Use constructor injection to avoid deprecation warnings in Spring Security 6.x
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        // Set using methods (still works, just deprecated warning)
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

//    @Bean
//    public AuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
//        authenticationProvider.setUserDetailsService(userDetailsService(passwordEncoder()));
//        authenticationProvider.setPasswordEncoder(passwordEncoder());
//        return authenticationProvider;
//    }
//
//    @Bean
//    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
//        UserDetails admin = User.withUsername("admin")
//                .password(encoder.encode("123"))
//                .roles("ADMIN")
//                .build();
//        UserDetails user = User.withUsername("user")
//                .password(encoder.encode("123"))
//                .roles("USER")
//                .build();
//        return new InMemoryUserDetailsManager(admin, user);
//    }

    // phân quyền
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//         cấu hình có thể logout
        http.csrf(AbstractHttpConfigurer::disable);
        // tạo token cho method post
//        http.csrf(Customizer.withDefaults());
//        http.csrf((csrf) -> csrf
//                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//                );

        // Hợp nhất tất cả các quy tắc phân quyền vào MỘT khối
        http.authorizeHttpRequests((authorize) -> authorize

                // 1. Các file static (Luôn luôn cho phép)
                .requestMatchers("/css/**", "/js/**", "/images/**", "/vendor/**").permitAll()

                // 2. Các trang công khai (Không cần đăng nhập)
                .requestMatchers("/", "/home", "/login", "/register", "/create-admin-account", "/reset-admin-password", "/create-test-user", "/test/**", "/debug/**", "/403").permitAll()

                // 3. Các trang của ADMIN (Quy tắc cụ thể)
                .requestMatchers("/admin", "/admin/**").hasRole("ADMIN") // Dùng hasAuthority

                // 4. Các trang của USER (Ý định MỚI của bạn)
                // Đã gộp /home, /auction/**, /blog/create vào đây
                .requestMatchers("/auction/**", "/create","/search/**","/search").hasAnyRole("USER", "ADMIN")
                // Dùng hasAuthority
                // 5. Tất cả các yêu cầu còn lại phải được xác thực (đăng nhập)
                // (Nếu bạn muốn /home và /auction là public, hãy chuyển chúng lên mục số 2)
                .anyRequest().authenticated()
        );
        // Inject AuthenticationProvider vào SecurityFilterChain
        http.authenticationProvider(authenticationProvider());

        // cấu hình form login
        http.formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/process-login") // đường dẫn trùng với url form login
                .defaultSuccessUrl("/home", true) // force redirect
                .failureUrl("/login?error=true") // thêm error parameter
                .usernameParameter("username")//trùng với tên trong form đăng nhập
                .passwordParameter("password")// trung với tên trong form đăng nhập
                .permitAll()
        );
        // cấu hình logout
        http.logout(form -> form.logoutUrl("/logout").logoutSuccessUrl("/home"));

        // cấu hình trả về trang 403 khi không có quyền (role) truy cập
        http.exceptionHandling(ex -> ex.accessDeniedPage("/403"));
        return http.build();

    }

}