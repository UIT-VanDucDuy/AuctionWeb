package loipt.example.autionweb1.repository;

import com.example.auctionweb.entity.AuctionRegistration;
import com.example.auctionweb.entity.AuctionRegistration.RegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuctionRegistrationRepository extends JpaRepository<AuctionRegistration, Integer> {
    List<AuctionRegistration> findByStatus(RegistrationStatus status);
}



