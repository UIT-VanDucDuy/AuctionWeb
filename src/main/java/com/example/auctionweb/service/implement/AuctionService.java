package com.example.auctionweb.service.implement;

import com.example.auctionweb.dto.AuctionDto;
import com.example.auctionweb.entity.*;
import com.example.auctionweb.repository.AuctionRegistrationRepository;
import com.example.auctionweb.repository.AuctionRepository;
import com.example.auctionweb.repository.ProductRepository;
import com.example.auctionweb.service.interfaces.IAuctionService;
import com.example.auctionweb.service.interfaces.INotificationService;
import com.example.auctionweb.websocket.BidWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuctionService implements IAuctionService {
    @Autowired
    private AuctionRepository auctionRepository;
    @Autowired
    private AuctionRegistrationRepository auctionRegistrationRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private BidHistoryService bidHistoryService;
    @Autowired
    private INotificationService notificationService;
    @Autowired
    @Lazy
    private BidWebSocketHandler bidWebSocketHandler;
    @Override
    public Auction getAuctionById(int id) {
        return auctionRepository.findById(id).get();
    }

    @Override
    public AuctionDto getAuctionInfoById(int id) {
        Auction auction = auctionRepository.findById(id).get();
        List<BidHistory> bidHistories = bidHistoryService.findByAuction(auction);
        BigDecimal highestBidPrice;
        List<AuctionRegistration> auctionRegistrations = auctionRegistrationRepository.findByAuctionAndStatus(auction, AuctionRegistration.RegistrationStatus.APPROVED);
        List<User> users = new ArrayList<>();
        for (AuctionRegistration auctionRegistration : auctionRegistrations) {
            User user = auctionRegistration.getUser();
            users.add(user);
        }
        if(bidHistories.isEmpty()){
            highestBidPrice = auction.getStartingPrice();
        }else {
            highestBidPrice = bidHistoryService.findTopByAuction(auction).getAmount();
        }
        return new AuctionDto(auction,bidHistories,
                highestBidPrice,users);
    }

    @Override
    public void finishExpiredAuctions() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        List<Auction> auctions = auctionRepository.findAllByStatusAndEndTimeBefore("ONGOING",now);
        for (Auction a : auctions) {
            a.setStatus("FINISHED");
            // tìm bid cao nhất
            BidHistory top = bidHistoryService.findTopByAuction(a);
            if (top != null) a.setWinner(top.getUser());
            auctionRepository.save(a);
            // Thông báo cho người bán (nếu có)
            if (a.getProduct() != null && a.getProduct().getSeller() != null) {
                Notification notificationSeller = new Notification(
                        a.getProduct().getSeller(),
                        "Bán thành công sản phẩm ID:" + a.getProduct().getId()
                );
                notificationService.save(notificationSeller);
            }
            // Thông báo cho người thắng (chỉ khi có winner)
            if (a.getWinner() != null) {
                Notification notificationNewOwner = new Notification(
                        a.getWinner(),
                        "Đấu giá thành công sản phẩm ID:" + a.getProduct().getId()
                );
                notificationService.save(notificationNewOwner);
            }
            // gửi thông báo qua WebSocket tới tất cả client
            bidWebSocketHandler.broadcastFinishAuction(a);
        }
    }

    @Override
    public void startAuction() {
        LocalDateTime now = LocalDateTime.now();
        List<Auction> auctions = auctionRepository.findAllByStatusAndStartTimeBefore("PENDING",now);
        for (Auction a : auctions) {
            a.setStatus("ONGOING");
            auctionRepository.save(a);
            if (a.getProduct() != null && a.getProduct().getSeller() != null) {
                Notification notificationSeller = new Notification(
                        a.getProduct().getSeller(),
                        "Bắt đầu đấu giá sản phẩm:" + a.getProduct().getId()
                );
                notificationService.save(notificationSeller);
            }
        }
    }

    @Override
    public List<Auction> getAuctionsByStatus(String status) {
        LocalDateTime now = LocalDateTime.now();
        List<Auction> auctions = auctionRepository.findAllByStatusAndStartTimeAfter(status,now);
        return auctions;
    }

    @Override
    public List<Auction> getAuctionsByCategory(Integer categoryId) {
        List<Auction> auctions = new ArrayList<>();
        List<Product> products = productRepository.findAllByCategory_Id(categoryId);
        for (Product p : products) {
        if(auctionRepository.findAllByProduct(p).getStatus().equals("ONGOING") ||
                auctionRepository.findAllByProduct(p).getStatus().equals("PENDING")){
                auctions.add(auctionRepository.findAllByProduct(p));
            }
        }
        return auctions;
    }
}
