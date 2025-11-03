package com.example.auctionweb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "account")
public class    Account {
    public enum Role {
        ADMIN, USER, SELLER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 50, nullable = false, unique = true)
    private String username;

    @Column(length = 255, nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private Role role;

    @Column(name = "active")
    private Boolean active = true;

    public Account() {
    }

}