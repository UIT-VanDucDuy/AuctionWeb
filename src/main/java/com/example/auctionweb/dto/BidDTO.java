package com.example.auctionweb.dto;

import java.math.BigDecimal;

public class BidDTO {
    private String bidderName;
    private BigDecimal amount;
    private String timeText;

    public BidDTO(String bidderName, BigDecimal amount, String timeText) {
        this.bidderName = bidderName;
        this.amount = amount;
        this.timeText = timeText;
    }

    public String getBidderName() { return bidderName; }
    public BigDecimal getAmount() { return amount; }
    public String getTimeText() { return timeText; }
}
