package com.example.auctionweb.service.interfaces;

import com.example.auctionweb.dto.AuctionDto;
import com.example.auctionweb.entity.Auction;

import java.util.List;

public interface IAuctionService {
    Auction getAuctionById(int id);
    AuctionDto getAuctionInfoById(int productId);
    void finishExpiredAuctions() throws Exception;
    void startAuction();
    List<Auction> getAuctionsByStatus(String status);
    List<Auction> getAuctionsByCategory(Integer categoryId);
}
