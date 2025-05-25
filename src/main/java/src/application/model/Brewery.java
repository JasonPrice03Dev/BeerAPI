package src.application.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Alan.Ryan
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class Brewery implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String code;
    private String country;
    private String phone;
    private String website;
    private String image;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer add_user;

    @Temporal(TemporalType.TIMESTAMP)
    private Date last_mod;

    private Double credit_limit;
    private String email;

    @OneToOne
    @JoinColumn(name = "geocode_id")
    @JsonBackReference
    @ToString.Exclude
    private Breweries_Geocode geocode;

    @OneToMany(mappedBy = "brewery", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    @ToString.Exclude
    private List<Beer> beers;

    // Missing Variables which were added

    public String getAddress() {
        return address1 + ", " + city + ", " + state + ", " + country;
    }

    public String getContact() {
        return phone + " | " + email;
    }

    public Double getLatitude() {
        return geocode != null ? geocode.getLatitude() : null;
    }

    public Double getLongitude() {
        return geocode != null ? geocode.getLongitude() : null;
    }

}

