package src.application.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class BeerWishListItem {
    // Entity class for the wish listed items (Whether tasted, reviewed, date added etc.)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "wishlist_id", referencedColumnName = "id")
    @JsonBackReference
    private BeerWishList beerWishList;

    @ManyToOne
    @JoinColumn(name = "beer_id", referencedColumnName = "id")
    private Beer beer;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Integer rating;
    private LocalDateTime addedAt;

    public BeerWishListItem(BeerWishList beerWishList, Beer beer, Status status) {
        this.beerWishList = beerWishList;
        this.beer = beer;
        this.status = status;
        this.addedAt = LocalDateTime.now();
    }
    public enum Status {
        WISHLIST,
        TASTED
    }
}


