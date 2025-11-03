package com.example.auctionweb.repository;

import com.example.auctionweb.entity.Auction;
import com.example.auctionweb.entity.AuctionRegistration;
import com.example.auctionweb.entity.AuctionRegistration.RegistrationStatus;
import com.example.auctionweb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuctionRegistrationRepository extends JpaRepository<AuctionRegistration, Integer> {
    List<AuctionRegistration> findByStatus(RegistrationStatus status);
    long countByStatus(RegistrationStatus status);
    List<AuctionRegistration> findByAuctionAndStatus(Auction auction, RegistrationStatus status);
}



