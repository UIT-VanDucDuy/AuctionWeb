package com.example.auctionweb.service.interfaces;

import com.example.auctionweb.entity.Account;

public interface IAccountService {
    Account getAccount(String username);
}
