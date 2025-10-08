package com.example.auctionweb.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bidhistory")
public class BidHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JoinColumn(name = "auction_id", nullable = false)
    private int auctionId;

    @JoinColumn(name = "user_id", nullable = false)
    private int userId;

    @Column(precision = 15, scale = 2, nullable = false)
    private int amount;

    @Column
    private LocalDateTime time;

    @Column(name = "winner_flag")
    private Boolean winnerFlag;

    public BidHistory() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(int auctionId) {
        this.auctionId = auctionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public Boolean getWinnerFlag() {
        return winnerFlag;
    }

    public void setWinnerFlag(Boolean winnerFlag) {
        this.winnerFlag = winnerFlag;
    }
}
