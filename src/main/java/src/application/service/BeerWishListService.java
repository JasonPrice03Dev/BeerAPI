package src.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import src.application.model.*;
import src.application.repository.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class BeerWishListService {

    @Autowired
    private BeerWishListRepository beerWishListRepository;

    @Autowired
    private BeerWishListItemRepository beerWishlistItemRepository;

    @Autowired
    private ArchivedWishlistRepository archivedWishlistRepository;

    @Autowired
    private BeerRepository beerRepository;

    // Add a beer to the wishlist
    public BeerWishListItem addBeerToWishlist(Long userId, Long beerId) {
        Optional<BeerWishList> beerWishListOpt = beerWishListRepository.findByUserId(userId);
        Optional<Beer> beer = beerRepository.findById(beerId);

        if (beer.isEmpty()) {
            return null;
        }

        BeerWishList beerWishList = beerWishListOpt.orElseGet(() -> {
            User user = new User();
            user.setId(userId);
            BeerWishList newWishList = new BeerWishList(user);
            return beerWishListRepository.save(newWishList);
        });

        Optional<BeerWishListItem> existingItem = beerWishlistItemRepository.findByBeerIdAndBeerWishList_Id(beerId, beerWishList.getId());
        if (existingItem.isPresent()) {
            return existingItem.get();
        }

        BeerWishListItem item = new BeerWishListItem(beerWishList, beer.get(), BeerWishListItem.Status.WISHLIST);
        return beerWishlistItemRepository.save(item);
    }

    // Remove a beer from the wishlist
    public Boolean removeBeerFromWishlist(Long userId, Long beerId) {
        Optional<BeerWishList> beerWishListOpt = beerWishListRepository.findByUserId(userId);
        if (beerWishListOpt.isEmpty()) {
            return null;
        }

        BeerWishList beerWishList = beerWishListOpt.get();
        Optional<BeerWishListItem> itemOpt = beerWishlistItemRepository.findByBeerIdAndBeerWishList_Id(beerId, beerWishList.getId());

        if (itemOpt.isEmpty()) {
            return null;
        }

        beerWishlistItemRepository.delete(itemOpt.get());
        return true;
    }

    @Transactional
    public boolean ArchiveBeerFromWishlist(Long userId, Long beerId) {
        Optional<BeerWishList> wishlistOpt = beerWishListRepository.findByUserId(userId);
        if (wishlistOpt.isEmpty()) {
            return false;
        }

        BeerWishList wishlist = wishlistOpt.get();

        Optional<BeerWishListItem> itemToArchiveOpt = wishlist.getItems().stream()
                .filter(item -> Objects.equals(item.getBeer().getId(), beerId))
                .findFirst();

        if (itemToArchiveOpt.isEmpty()) {
            return false;
        }

        BeerWishListItem itemToArchive = itemToArchiveOpt.get();

        ArchivedWishlist archivedWishlist = new ArchivedWishlist(wishlist);
        archivedWishlist.setItems(List.of(new ArchivedWishlistItem(itemToArchive, archivedWishlist)));

        archivedWishlistRepository.save(archivedWishlist);

        wishlist.getItems().remove(itemToArchive);
        beerWishListRepository.save(wishlist);

        return true;
    }

    // View all beers in a user's wishlist
    public Page<BeerWishListItem> getWishlist(Long userId, Pageable pageable) {
        Optional<BeerWishList> beerWishListOpt = beerWishListRepository.findByUserId(userId);
        if (beerWishListOpt.isEmpty()) {
            return null;
        }

        return beerWishlistItemRepository.findByBeerWishListId(beerWishListOpt.get().getId(), pageable);
    }

    // Mark a beer as tasted and add a rating
    public BeerWishListItem markAsTasted(Long userId, Long beerId, Integer rating) {
        Optional<BeerWishList> beerWishListOpt = beerWishListRepository.findByUserId(userId);
        if (beerWishListOpt.isEmpty()) {
            return null;
        }

        Optional<BeerWishListItem> itemOpt = beerWishlistItemRepository.findByBeerIdAndBeerWishList_Id(beerId, beerWishListOpt.get().getId());
        if (itemOpt.isEmpty()) {
            return null;
        }

        BeerWishListItem item = itemOpt.get();
        item.setStatus(BeerWishListItem.Status.TASTED);
        item.setRating(rating);
        beerWishlistItemRepository.save(item);

        return item;
    }
}
