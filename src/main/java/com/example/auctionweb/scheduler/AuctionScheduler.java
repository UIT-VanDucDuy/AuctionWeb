package com.example.auctionweb.scheduler;

import com.example.auctionweb.service.IAuctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class AuctionScheduler {
    @Autowired
    private IAuctionService auctionService;

    @Scheduled(fixedRate = 1000) // mỗi 1 giây
    public void checkAuctionEndTimes() {
        auctionService.finishExpiredAuctions();
    }
}
