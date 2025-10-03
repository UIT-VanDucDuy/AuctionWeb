package loipt.example.autionweb1.repository;

import com.example.auctionweb.entity.Auction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionRepository extends JpaRepository<Auction, Integer> {
}



