package src.application.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class ArchivedWishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "beer_id")
    private Long beerId;

    private Long originalWishlistId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "archivedWishlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArchivedWishlistItem> items;

    @Temporal(TemporalType.TIMESTAMP)
    private Date archivedAt;

    public ArchivedWishlist(BeerWishList wishlist) {
        this.originalWishlistId = wishlist.getId();
        this.user = wishlist.getUser();
        this.archivedAt = new Date();
    }
}
