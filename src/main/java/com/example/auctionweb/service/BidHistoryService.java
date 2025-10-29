package com.example.auctionweb.service;

import com.example.auctionweb.entity.Account;
import com.example.auctionweb.entity.Auction;
import com.example.auctionweb.entity.BidHistory;
import com.example.auctionweb.repository.BidHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class BidHistoryService implements IBidHistoryService {
    @Autowired
    private BidHistoryRepository bidHistoryRepository;
    @Override
    public List<BidHistory> findAll() {
        return bidHistoryRepository.findAllByOrderByTimeDesc();
    }

    @Override
    public BidHistory save(BidHistory bidHistory) {
        return bidHistoryRepository.save(bidHistory);
    }

    @Override
    public List<BidHistory> findByAuction(Auction auction) {
        return bidHistoryRepository.findBidHistoriesByAuctionOrderByTimeDesc(auction);
    }
    @Override
    public BidHistory findTopByAuction(Auction auction) {
        return bidHistoryRepository.findTopByAuctionOrderByAmountDesc(auction);
    }

}
