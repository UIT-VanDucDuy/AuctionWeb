package com.example.auctionweb.service.interfaces;

import com.example.auctionweb.entity.Account;
import com.example.auctionweb.entity.User;

public interface IUserService {
    User findUserById(int id);
    User findUserByAccount(Account account);
}
