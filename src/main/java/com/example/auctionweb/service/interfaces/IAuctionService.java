package com.example.auctionweb.service.interfaces;

import com.example.auctionweb.dto.AuctionDto;
import com.example.auctionweb.entity.Auction;

public interface IAuctionService {
    Auction getAuctionById(int id);
    AuctionDto getAuctionInfoById(int productId);
    void finishExpiredAuctions() throws Exception;
    void startAuction();
}
