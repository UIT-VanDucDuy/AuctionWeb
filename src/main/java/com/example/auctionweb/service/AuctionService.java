package com.example.auctionweb.service;

import com.example.auctionweb.dto.AuctionDto;
import com.example.auctionweb.entity.Auction;
import com.example.auctionweb.entity.BidHistory;
import com.example.auctionweb.entity.Notification;
import com.example.auctionweb.entity.Product;
import com.example.auctionweb.repository.AuctionRepository;
import com.example.auctionweb.websocket.BidWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
public class AuctionService implements IAuctionService {
    @Autowired
    private AuctionRepository auctionRepository;
    @Autowired
    private BidHistoryService bidHistoryService;
    @Autowired
    private INotificationService notificationService;
    @Override
    public Auction getAuctionById(int id) {
        return auctionRepository.findById(id).get();
    }

    @Override
    public AuctionDto getAuctionInfoById(int id) {
        Auction auction = auctionRepository.findById(id).get();
        List<BidHistory> bidHistories = bidHistoryService.findByAuction(auction);
        return new AuctionDto(auction.getId(),auction.getProduct(),auction.getStartTime(),
                auction.getEndTime(),auction.getStartingPrice(),bidHistories,
                bidHistoryService.findTopByAuction(auction).getAmount(),auction.getStatus());
    }

    @Override
    public void finishExpiredAuctions() {
        List<Auction> auctions = auctionRepository.findAllByStatusAndEndTimeBefore("ONGOING", LocalDateTime.now());
        for (Auction a : auctions) {
            a.setStatus("FINISHED");
            // tìm bid cao nhất
            BidHistory top = bidHistoryService.findTopByAuction(a);
            if (top != null) a.setWinner(top.getUser());
            auctionRepository.save(a);
            Notification notificationSeller = new Notification(a.getProduct().getSeller(),"Bán thành công sản phẩm ID:" + a.getProduct().getId().toString());
            notificationService.save(notificationSeller);
            Notification notificationNewOwner = new Notification(a.getWinner(),"Đấu giá thành công sản phẩm ID:" + a.getProduct().getId().toString());
            notificationService.save(notificationNewOwner);
            // gửi thông báo qua WebSocket tới tất cả client
            //bidWebSocketHandler.broadcastMessage("AUCTION_END");
        }
    }
}
