package com.example.auctionweb.repository;

import com.example.auctionweb.entity.Auction;
import com.example.auctionweb.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AuctionRepository extends JpaRepository<Auction, Integer> {
    List<Auction> findAllByStatusAndEndTimeBefore(String status, LocalDateTime endTime);
    List<Auction> findAllByStatusAndStartTimeBefore(String status, LocalDateTime startTime);
    Auction findAllByProduct(Product product);

    List<Auction> findAllByStatusAndStartTimeAfter(String status, LocalDateTime startTime);
}



