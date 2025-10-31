package com.example.auctionweb.service.implement;

import com.example.auctionweb.entity.Account;
import com.example.auctionweb.entity.User;
import com.example.auctionweb.repository.UserRepository;
import com.example.auctionweb.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public User findUserById(int id) {
        return userRepository.findById(id).get();
    }

    @Override
    public User findUserByAccount(Account account) {
        return userRepository.findByAccount(account);
    }
}
