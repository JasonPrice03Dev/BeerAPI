package src.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import src.application.model.Beer;

import java.util.List;

@Repository
public interface BeerRepository extends JpaRepository<Beer, Long> {

    // Query to check if there is stock or a sale is happening
    @Query("SELECT b FROM Beer b WHERE b.inStock = true OR b.onSale = true")
    List<Beer> findBeersOnSaleOrInStock();
}
