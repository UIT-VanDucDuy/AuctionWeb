package com.example.auctionweb.repository;

import com.example.auctionweb.entity.Auction;
import com.example.auctionweb.entity.BidHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BidHistoryRepository extends JpaRepository<BidHistory, Integer> {
    List<BidHistory> findAll();
    // save() method is already inherited from JpaRepository, no need to redeclare
    List<BidHistory> findAllByOrderByTimeDesc();
    List<BidHistory> findBidHistoriesByAuctionOrderByTimeDesc(Auction auction);
    BidHistory findTopByAuctionOrderByAmountDesc(Auction auction);
}
