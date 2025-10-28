package com.example.auctionweb.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter
@Setter
@Entity
@Table(name = "bidhistory")
public class BidHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "auction_id", nullable = false)
    private Auction auction;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column
    private LocalDateTime time;
    @Column(name = "winner_flag")
    private Boolean winnerFlag;
    public BidHistory() {
        this.time = LocalDateTime.now();
    }

    public BidHistory(Auction auction, User user, BigDecimal  amount) {
        this.auction = auction;
        this.user = user;
        this.amount = amount;
        this.time = LocalDateTime.now();
    }
}
