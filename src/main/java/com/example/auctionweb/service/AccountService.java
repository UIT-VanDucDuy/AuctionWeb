package com.example.auctionweb.service;

import com.example.auctionweb.entity.Account;
import com.example.auctionweb.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService implements IAccountService {
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public Account getAccount(String username) {
        return accountRepository.findByUsername(username);
    }

}
