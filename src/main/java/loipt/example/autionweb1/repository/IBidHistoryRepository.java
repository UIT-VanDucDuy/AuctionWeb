package loipt.example.autionweb1.repository;

import com.example.auctionweb.entity.BidHistory;

import java.util.List;

public interface IBidHistoryRepository {
    List<BidHistory> findAll();
    boolean add(BidHistory bidHistory);
}
