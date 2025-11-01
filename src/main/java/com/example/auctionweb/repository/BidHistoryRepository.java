package com.example.auctionweb.repository;

import com.example.auctionweb.entity.Auction;
import com.example.auctionweb.entity.BidHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface BidHistoryRepository extends JpaRepository<BidHistory, Integer> {
    List<BidHistory> findAll();
    BidHistory save(BidHistory bidHistory);
    List<BidHistory> findAllByOrderByTimeDesc();

    List<BidHistory> findBidHistoriesByAuctionOrderByTimeDesc(com.example.auctionweb.entity.Auction auction);

    BidHistory findTopByAuctionOrderByAmountDesc(com.example.auctionweb.entity.Auction auction);

    @Query("""
           SELECT MAX(b.amount)
           FROM BidHistory b
           WHERE b.auction.product.id = :productId
           """)
    BigDecimal findMaxAmountByProductId(@Param("productId") Integer productId);

    @Query("""
           SELECT b.auction.product.id, MAX(b.amount)
           FROM BidHistory b
           WHERE b.auction.product.id IN :productIds
           GROUP BY b.auction.product.id
           """)
    List<Object[]> findMaxAmountByProductIds(@Param("productIds") List<Integer> productIds);
}
