package loipt.example.autionweb1.repository;

import com.example.auctionweb.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}



