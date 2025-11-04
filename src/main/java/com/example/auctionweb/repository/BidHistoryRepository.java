package com.example.auctionweb.repository;

import com.example.auctionweb.entity.Auction;
import com.example.auctionweb.entity.BidHistory;
import com.example.auctionweb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface BidHistoryRepository extends JpaRepository<BidHistory, Integer> {

    List<BidHistory> findAllByOrderByTimeDesc();
    List<BidHistory> findBidHistoriesByAuctionOrderByTimeDesc(Auction auction);
    BidHistory findTopByAuctionOrderByAmountDesc(Auction auction);

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

    List<BidHistory> findByAuction_IdOrderByTimeDesc(Integer auctionId);

    @Query("""
           SELECT COALESCE(MAX(b.amount), 0)
           FROM BidHistory b
           WHERE b.auction.id = :auctionId
           """)
    BigDecimal findMaxAmountByAuctionId(@Param("auctionId") Integer auctionId);

    @Query("""
           SELECT b.auction.id, COALESCE(MAX(b.amount), 0)
           FROM BidHistory b
           WHERE b.auction.id IN :auctionIds
           GROUP BY b.auction.id
           """)
    List<Object[]> findMaxAmountByAuctionIds(@Param("auctionIds") List<Integer> auctionIds);
    @Query("""
       SELECT COUNT(b)
       FROM BidHistory b
       WHERE b.auction.id = :auctionId
       """)
    long countByAuctionId(@Param("auctionId") Integer auctionId);

    @Query("""
       SELECT b.auction.id, COUNT(b)
       FROM BidHistory b
       WHERE b.auction.id IN :auctionIds
       GROUP BY b.auction.id
       """)
    List<Object[]> countByAuctionIds(@Param("auctionIds") List<Integer> auctionIds);

    List<BidHistory> findByUserOrderByTimeDesc(User user);
}
