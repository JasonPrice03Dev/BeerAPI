package src.application.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BreweryInsightsDTO {
    // Information regarding brewery
    private String name;

    private String location;

    private String contact;

    private Double latitude;

    private Double longitude;

    private BigDecimal avgRating;

    private int numberOfReviews;

    private List<BeerInfo> beers;

    // class representing details of individual beers from a brewery
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BeerInfo {
        private String beerName;

        private String style;

        private BigDecimal abv;
    }
}
