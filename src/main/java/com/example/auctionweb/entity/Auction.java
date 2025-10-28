package com.example.auctionweb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "auction")
public class Auction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @OneToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    @Column(name = "start_time")
    private LocalDateTime startTime;
    @Column(name = "end_time")
    private LocalDateTime endTime;
    @Column(name = "starting_price", precision = 15, scale = 2)
    private BigDecimal startingPrice;
    @Column(name = "status")
    private String status;
    @OneToOne
    @JoinColumn(name = "winner")
    private User winner;

    public Auction() {
    }
}
