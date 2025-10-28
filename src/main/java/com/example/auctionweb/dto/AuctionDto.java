package com.example.auctionweb.dto;

import com.example.auctionweb.entity.BidHistory;
import com.example.auctionweb.entity.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class AuctionDto {
    private int id;
    private Product product;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal startingPrice;
    private List<BidHistory> bidHistories;
    private BigDecimal highestBidPrice;
    private String status;

    public AuctionDto(int id, Product product, LocalDateTime startTime, LocalDateTime endTime,
                      BigDecimal startingPrice, List<BidHistory> bidHistories, BigDecimal highestBidPrice, String status) {
        this.id = id;
        this.product = product;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startingPrice = startingPrice;
        this.bidHistories = bidHistories;
        this.highestBidPrice = highestBidPrice;
        this.status = status;
    }
}
