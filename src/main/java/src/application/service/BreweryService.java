package src.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import src.application.DTO.BreweryInsightsDTO;
import src.application.exception.BreweryNotFoundException;
import src.application.model.Beer;
import src.application.model.Brewery;
import src.application.model.Review;
import src.application.repository.BreweryRepository;
import src.application.repository.ReviewRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class BreweryService {
    private final BreweryRepository breweryRepository;
    private final ReviewRepository reviewRepository;

    private static final Logger log = LoggerFactory.getLogger(BreweryService.class);

    public BreweryService(BreweryRepository breweryRepository, ReviewRepository reviewRepository) {
        this.breweryRepository = breweryRepository;
        this.reviewRepository = reviewRepository;
    }


    // Converts a Beer entity into a BeerInfo DTO
    private BreweryInsightsDTO.BeerInfo mapBeerToBeerInfo(Beer beer) {
        return new BreweryInsightsDTO.BeerInfo(beer.getName(), beer.getStyle().getStyle_name(), BigDecimal.valueOf(beer.getAbv()));
    }


    // Calculates the average rating of a list of reviews
    private BigDecimal calculateAverageRating(List<Review> reviews) {
        if (reviews.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return reviews.stream().map(review -> BigDecimal.valueOf(review.getRating())).reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(reviews.size()), RoundingMode.HALF_UP);
    }

    // Retrieves report of Brewery
    public BreweryInsightsDTO getBreweryReport(Long breweryId) {
        Brewery brewery = breweryRepository.findById(breweryId).orElse(null);

        // If brewery is not found return null
        if (brewery == null) {
            log.warn("Brewery with ID {} not found", breweryId);
            return null;
        }

        List<Review> reviews = reviewRepository.findByBeer_Brewery_Id(breweryId);

        BigDecimal avgRating = calculateAverageRating(reviews);


        // Extracts a list of beers that have been reviewed
        List<BreweryInsightsDTO.BeerInfo> beers = brewery.getBeers().stream().filter(beer -> !beer.getReviews().isEmpty()).map(this::mapBeerToBeerInfo).collect(Collectors.toList());

        if (beers.isEmpty()) {
            beers.add(new BreweryInsightsDTO.BeerInfo("No beers reviewed", "N/A", BigDecimal.ZERO));
        }

        Double latitude = brewery.getLatitude() != null ? brewery.getLatitude().doubleValue() : 0.0;
        Double longitude = brewery.getLongitude() != null ? brewery.getLongitude().doubleValue() : 0.0;

        // Constructs and returns the brewery report
        return new BreweryInsightsDTO(brewery.getName(), brewery.getAddress(), brewery.getContact(), brewery.getLatitude(), brewery.getLongitude(), avgRating, reviews.size(), beers);
    }
}
