package com.example.auctionweb.dto;

import com.example.auctionweb.entity.Auction;
import com.example.auctionweb.entity.BidHistory;
import com.example.auctionweb.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class AuctionDto {
    private int id;
    private Auction auction;
    private List<BidHistory> bidHistories;
    private BigDecimal highestBidPrice;
    private List<User> users;
    public AuctionDto(Auction auction,List<BidHistory> bidHistories, BigDecimal highestBidPrice,List<User> users) {
        this.auction = auction;
        this.bidHistories = bidHistories;
        this.highestBidPrice = highestBidPrice;
        this.users = users;
    }
}
