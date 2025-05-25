package src.application.DTO;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
@JsonRootName("customerBeerExperience")
@NoArgsConstructor
public class CustomerBeerExperienceDTO {
    // Information regarding customer
    private String name;
    private String email;
    private List<BeerReviewDTO> beerReviews;

    // Class representing details of beer reviews from a customer
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BeerReviewDTO {
        private String beerName;
        private String beerStyle;
        private BigDecimal abv;
        private String category;
        private String breweryName;
        private String country;
        private String state;
        private Integer rating;
        private String comment;
        private String reviewDate;
    }
}
