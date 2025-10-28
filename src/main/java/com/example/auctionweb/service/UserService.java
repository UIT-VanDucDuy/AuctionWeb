package com.example.auctionweb.service;

import com.example.auctionweb.entity.Account;
import com.example.auctionweb.entity.User;
import com.example.auctionweb.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountService accountService;

    @Override
    public User findUserById(int id) {
        return userRepository.findById(id).get();
    }

    @Override
    public User findUserByAccount(Account account) {
        return userRepository.findByAccount(account);
    }
}
