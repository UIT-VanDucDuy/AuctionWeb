package com.example.auctionweb.service;

import com.example.auctionweb.entity.Auction;
import com.example.auctionweb.entity.BidHistory;

import java.util.List;

public interface IBidHistoryService {
    List<BidHistory> findAll();
    BidHistory save(BidHistory bidHistory);
    List<BidHistory> findByAuction(Auction auction);
    BidHistory findTopByAuction(Auction auction);
}
