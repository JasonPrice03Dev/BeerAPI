package src.application.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import src.application.model.BeerWishListItem;

import java.util.List;
import java.util.Optional;

public interface BeerWishListItemRepository extends JpaRepository<BeerWishListItem, Long> {
    Page<BeerWishListItem> findByBeerWishListId(Long beerWishListId, Pageable pageable);

    Optional<BeerWishListItem> findByBeerIdAndBeerWishList_Id(Long beerId, Long wishListId);
}
