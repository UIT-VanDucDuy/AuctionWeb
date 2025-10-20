package com.example.auctionweb.repository;

import com.example.auctionweb.entity.Notification;
import com.example.auctionweb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUserOrderByTimeDesc(User user);
}



