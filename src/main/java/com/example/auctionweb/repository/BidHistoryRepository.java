package com.example.auctionweb.repository;

import com.example.auctionweb.entity.BidHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BidHistoryRepository extends JpaRepository<BidHistory, Integer> {
    List<BidHistory> findAll();
    BidHistory save(BidHistory bidHistory);
    List<BidHistory> findAllByOrderByTimeDesc();
    List<BidHistory> findBidHistoriesByAuctionIdOrderByTimeDesc(int auctionId);
}
