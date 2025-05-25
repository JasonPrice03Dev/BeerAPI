package src.application.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class Beer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name = "brewery_id")
    @JsonBackReference
    @ToString.Exclude
    private Brewery brewery;

    @ManyToOne
    @JoinColumn(name = "cat_id")
    @JsonBackReference
    @ToString.Exclude
    private Category category;

    @ManyToOne
    @JoinColumn(name = "style_id")
    @JsonBackReference
    @ToString.Exclude
    private Style style;

    @OneToMany(mappedBy = "beer", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @ToString.Exclude
    private List<Review> reviews;

    private String name;
    private Double abv;
    private Double ibu;
    private Double srm;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer add_user;

    @Temporal(TemporalType.TIMESTAMP)
    private Date last_mod;

    private String image;
    private Double buy_price;
    private Double sell_price;
    private Double averageRating;

    // Added boolean variables for inStock and onSale
    private boolean inStock;
    private boolean onSale;
}
