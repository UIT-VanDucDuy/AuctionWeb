package loipt.example.autionweb1.service;

import com.example.auctionweb.entity.BidHistory;

import java.util.List;

public interface IBidHistoryService {
    List<BidHistory> findAll();
    boolean add(BidHistory bidHistory);
}
