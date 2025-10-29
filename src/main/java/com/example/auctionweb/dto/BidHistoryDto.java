package com.example.auctionweb.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
public class BidHistoryDto {
    private int auctionId;
    private int userId;
    private BigDecimal amount;

    public BidHistoryDto(int auctionId, int userId, BigDecimal price) {
        this.auctionId = auctionId;
        this.userId = userId;
        this.amount = price;
    }
}
