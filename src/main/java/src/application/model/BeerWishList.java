package src.application.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class BeerWishList {
    // Entity class for the beer wishlist of a user
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy = "beerWishList", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<BeerWishListItem> items;

    public BeerWishList(User user) {
        this.user = user;
    }
}
