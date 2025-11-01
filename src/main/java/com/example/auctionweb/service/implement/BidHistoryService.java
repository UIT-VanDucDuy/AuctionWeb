package com.example.auctionweb.service.implement;

import com.example.auctionweb.entity.Auction;
import com.example.auctionweb.entity.BidHistory;
import com.example.auctionweb.repository.BidHistoryRepository;
import com.example.auctionweb.service.interfaces.IBidHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BidHistoryService implements IBidHistoryService {
    @Autowired
    private BidHistoryRepository bidHistoryRepository;
    @Override
    public List<BidHistory> findAll() {
        return bidHistoryRepository.findAllByOrderByTimeDesc();
    }

    @Override
    public BidHistory save(BidHistory bidHistory) {
        return bidHistoryRepository.save(bidHistory);
    }

    @Override
    public List<BidHistory> findByAuction(Auction auction) {
        return bidHistoryRepository.findBidHistoriesByAuctionOrderByTimeDesc(auction);
    }
    @Override
    public BidHistory findTopByAuction(Auction auction) {
        return bidHistoryRepository.findTopByAuctionOrderByAmountDesc(auction);
    }
    public BigDecimal getHighestBidByProductId(Integer productId) {
        return bidHistoryRepository.findMaxAmountByProductId(productId);
    }


    public Map<Integer, BigDecimal> getHighestBidMapByProductIds(List<Integer> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Object[]> rows = bidHistoryRepository.findMaxAmountByProductIds(productIds);
        Map<Integer, BigDecimal> result = new HashMap<>();
        for (Object[] r : rows) {
            Integer pid = (Integer) r[0];
            BigDecimal max = (BigDecimal) r[1];
            result.put(pid, max);
        }
        return result;
    }

}
