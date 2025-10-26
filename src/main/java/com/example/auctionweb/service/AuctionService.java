package com.example.auctionweb.service;

import com.example.auctionweb.dto.AuctionDto;
import com.example.auctionweb.entity.Auction;
import com.example.auctionweb.entity.BidHistory;
import com.example.auctionweb.entity.Product;
import com.example.auctionweb.repository.AuctionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class AuctionService implements IAuctionService {
    @Autowired
    private AuctionRepository auctionRepository;
    @Autowired
    private BidHistoryService bidHistoryService;

    @Override
    public Auction getAuctionById(int id) {
        return auctionRepository.findById(id).get();
    }

    @Override
    public AuctionDto getAuctionInfoById(int id) {
        Auction auction = auctionRepository.findById(id).get();
        List<BidHistory> bidHistories = bidHistoryService.findByAuctionId(id);
        return new AuctionDto(auction.getId(),auction.getProduct(),auction.getStartTime(),
                auction.getEndTime(),auction.getStartingPrice(),bidHistories);
    }
}
