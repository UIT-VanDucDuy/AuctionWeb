package com.example.auctionweb.repository;

import com.example.auctionweb.entity.Account;
import com.example.auctionweb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByAccount(Account account);
}



