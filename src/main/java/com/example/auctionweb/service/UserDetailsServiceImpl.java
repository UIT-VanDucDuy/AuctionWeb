package com.example.auctionweb.service;

import com.example.auctionweb.entity.Account;
import com.example.auctionweb.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private AccountRepository appUserRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Account account = this.appUserRepository.findByUsername(userName);
        if (account == null) {
            System.out.println("User not found! " + userName);
            throw new UsernameNotFoundException("User " + userName + " was not found in the database");
        }
        
        // Kiểm tra account có active không
        if (account.getActive() == null || !account.getActive()) {
            System.out.println("User account is INACTIVE: " + userName);
            throw new UsernameNotFoundException("User " + userName + " account is inactive");
        }
        
        System.out.println("Found User: " + account.getUsername() + " | Role: " + account.getRole() + " | Active: " + account.getActive());
        
        // Convert Role enum to String
        GrantedAuthority authority = new SimpleGrantedAuthority(account.getRole().name());
        return new User(
                account.getUsername(),
                account.getPassword(),
                account.getActive(), // enabled
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                Collections.singleton(authority)
        );
    }

}