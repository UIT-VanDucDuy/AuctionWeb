package com.example.auctionweb.repository;

import com.example.auctionweb.entity.Auction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AuctionRepository extends JpaRepository<Auction, Integer> {
    List<Auction> findAllByStatusAndEndTimeBefore(String status, LocalDateTime endTime);
}



