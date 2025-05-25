package src.application.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class ArchivedWishlistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "archived_wishlist_id", nullable = false)
    private ArchivedWishlist archivedWishlist;

    @ManyToOne
    @JoinColumn(name = "beer_id", nullable = false)
    private Beer beer;

    public ArchivedWishlistItem(BeerWishListItem item, ArchivedWishlist archivedWishlist) {
        this.beer = item.getBeer();
        this.archivedWishlist = archivedWishlist;
    }
}
