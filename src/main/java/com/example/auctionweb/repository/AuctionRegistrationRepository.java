package com.example.auctionweb.repository;

import com.example.auctionweb.entity.Auction;
import com.example.auctionweb.entity.AuctionRegistration;
import com.example.auctionweb.entity.AuctionRegistration.RegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AuctionRegistrationRepository extends JpaRepository<AuctionRegistration, Integer> {
    List<AuctionRegistration> findByStatus(RegistrationStatus status);
    long countByStatus(RegistrationStatus status);
    @Query("SELECT COUNT(ar) FROM AuctionRegistration ar WHERE ar.auction.id = :auctionId")
    long countByAuction_Id(Integer auctionId);
    @Query(value = "SELECT COUNT(*) FROM auctionregistration ar WHERE ar.auction_id = :auctionId", nativeQuery = true)
    long countRegsByAuction(@Param("auctionId") Integer auctionId);

    List<AuctionRegistration> findByAuctionAndStatus(Auction auction, RegistrationStatus registrationStatus);
}



