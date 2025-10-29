package com.example.auctionweb.service;

import com.example.auctionweb.entity.Notification;
import com.example.auctionweb.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService implements INotificationService {

    @Autowired
    private NotificationRepository notificationRepository;


    @Override
    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }
}
