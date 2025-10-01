package com.example.auctionweb.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class BidHistory {
    @Id
    private int id;
    private int auctionId;
    private int userId;
    private int amount;
    private String time;
    private boolean winnerFlag;
    public BidHistory() {
    }

    public BidHistory(int id, int auctionId, int userId, int amount) {
        this.id = id;
        this.auctionId = auctionId;
        this.userId = userId;
        this.amount = amount;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isWinnerFlag() {
        return winnerFlag;
    }

    public void setWinnerFlag(boolean winnerFlag) {
        this.winnerFlag = winnerFlag;
    }
}
