package src.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import src.application.model.Beer;
import src.application.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    // Used to find users and their beers
    @Query("SELECT DISTINCT bwl.user FROM BeerWishList bwl " +
            "JOIN bwl.items i WHERE i.beer = :beer")
    List<User> findUsersWithBeerInWishlist(Beer beer);
}
