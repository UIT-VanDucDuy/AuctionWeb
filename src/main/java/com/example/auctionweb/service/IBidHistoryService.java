package com.example.auctionweb.service;

import com.example.auctionweb.entity.BidHistory;

import java.util.List;

public interface IBidHistoryService {
    List<BidHistory> findAll();
    BidHistory save(BidHistory bidHistory);
    List<BidHistory> findByAuctionId(int id);
}
