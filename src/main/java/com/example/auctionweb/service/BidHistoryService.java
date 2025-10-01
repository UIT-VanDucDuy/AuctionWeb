package com.example.auctionweb.service;

import com.example.auctionweb.entity.BidHistory;
import com.example.auctionweb.repository.IBidHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class BidHistoryService implements IBidHistoryService {
    @Autowired
    private IBidHistoryRepository bidHistoryRepository;
    @Override
    public List<BidHistory> findAll() {
        return bidHistoryRepository.findAll();
    }

    @Override
    public boolean add(BidHistory bidHistory) {
        return bidHistoryRepository.add(bidHistory);
    }
}
