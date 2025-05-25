package src.application.model;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class Breweries_Geocode implements Serializable {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private long id;

   long brewery_id;
   private Double latitude;
   private Double longitude;


   @OneToOne(mappedBy = "geocode")
   @JsonManagedReference
   @ToString.Exclude
   private Brewery brewery;
}
