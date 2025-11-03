package com.example.auctionweb.service.implement;

import com.example.auctionweb.entity.Auction;
import com.example.auctionweb.entity.BidHistory;
import com.example.auctionweb.repository.BidHistoryRepository;
import com.example.auctionweb.service.interfaces.IBidHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

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

    @Override
    public BigDecimal getHighestBidByProductId(Integer productId) {
        return bidHistoryRepository.findMaxAmountByProductId(productId);
    }

    @Override
    public Map<Integer, BigDecimal> getHighestBidMapByProductIds(List<Integer> productIds) {
        if (productIds == null || productIds.isEmpty()) return Collections.emptyMap();
        List<Object[]> rows = bidHistoryRepository.findMaxAmountByProductIds(productIds);
        Map<Integer, BigDecimal> result = new HashMap<>();
        for (Object[] r : rows) {
            Integer pid = (Integer) r[0];
            BigDecimal max = (BigDecimal) r[1];
            result.put(pid, max);
        }
        return result;
    }

    @Override
    public List<BidHistory> findByAuctionId(Integer auctionId) {
        if (auctionId == null) return Collections.emptyList();
        return bidHistoryRepository.findByAuction_IdOrderByTimeDesc(auctionId);
    }

    @Override
    public BigDecimal getHighestBidByAuctionId(Integer auctionId) {
        if (auctionId == null) return BigDecimal.ZERO;
        BigDecimal max = bidHistoryRepository.findMaxAmountByAuctionId(auctionId);
        return max != null ? max : BigDecimal.ZERO;
    }

    @Override
    public Map<Integer, BigDecimal> getHighestBidMapByAuctionIds(List<Integer> auctionIds) {
        if (auctionIds == null || auctionIds.isEmpty()) return Collections.emptyMap();
        List<Object[]> rows = bidHistoryRepository.findMaxAmountByAuctionIds(auctionIds);
        Map<Integer, BigDecimal> result = new HashMap<>();
        for (Object[] r : rows) {
            Integer aid = (Integer) r[0];
            BigDecimal max = (BigDecimal) r[1];
            result.put(aid, max != null ? max : BigDecimal.ZERO);
        }
        for (Integer id : auctionIds) {
            result.putIfAbsent(id, BigDecimal.ZERO);
        }
        return result;
    }
    public Map<Integer, Long> getBidCountMapByAuctionIds(List<Integer> auctionIds) {
        if (auctionIds == null || auctionIds.isEmpty()) return Collections.emptyMap();
        List<Object[]> rows = bidHistoryRepository.countByAuctionIds(auctionIds);
        Map<Integer, Long> map = new HashMap<>();
        for (Object[] r : rows) {
            Integer aid = (Integer) r[0];
            Long cnt = (Long) r[1];
            map.put(aid, cnt);
        }
        return map;
    }

}
