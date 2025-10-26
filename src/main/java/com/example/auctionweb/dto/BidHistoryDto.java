package com.example.auctionweb.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class BidHistoryDto {
    private int auctionId;
    private int userId;
    private int amount;

    public BidHistoryDto(int auctionId, int userId, int price) {
        this.auctionId = auctionId;
        this.userId = userId;
        this.amount = price;
    }
}
