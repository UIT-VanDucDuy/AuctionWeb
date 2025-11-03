package com.example.auctionweb.service.interfaces;

import com.example.auctionweb.entity.Auction;
import com.example.auctionweb.entity.BidHistory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface IBidHistoryService {

    List<BidHistory> findAll();
    BidHistory save(BidHistory bidHistory);
    List<BidHistory> findByAuction(Auction auction);
    BidHistory findTopByAuction(Auction auction);

    BigDecimal getHighestBidByProductId(Integer productId);
    Map<Integer, BigDecimal> getHighestBidMapByProductIds(List<Integer> productIds);
    List<BidHistory> findByAuctionId(Integer auctionId);
    BigDecimal getHighestBidByAuctionId(Integer auctionId);
    Map<Integer, BigDecimal> getHighestBidMapByAuctionIds(List<Integer> auctionIds);
     Map<Integer, Long> getBidCountMapByAuctionIds(List<Integer> auctionIds);
}
