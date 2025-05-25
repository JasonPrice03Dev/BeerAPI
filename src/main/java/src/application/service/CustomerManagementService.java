package src.application.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import src.application.DTO.CustomerBeerExperienceDTO;
import src.application.exception.CustomerNotFoundException;
import src.application.model.*;
import src.application.repository.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerManagementService {
    private final CustomerRepository customerRepo;
    private final ReviewRepository reviewRepo;
    private final BeerRepository beerRepo;
    private final BreweryRepository breweryRepo;

    private static final Logger log = LoggerFactory.getLogger(CustomerManagementService.class);

    public CustomerManagementService(CustomerRepository customerRepo, ReviewRepository reviewRepo, BeerRepository beerRepo, BreweryRepository breweryRepo) {
        this.customerRepo = customerRepo;
        this.reviewRepo = reviewRepo;
        this.beerRepo = beerRepo;
        this.breweryRepo = breweryRepo;
    }

    // Retrieves a summary of a customer's beer experiences.
    public Page<CustomerBeerExperienceDTO> fetchCustomerExperience(Long customerId, Pageable pageable) {
        User customer = customerRepo.findById(customerId).orElse(null);

        if (customer == null) {
            log.warn("Customer with ID {} not found", customerId);
            return Page.empty();
        }

        Page<Review> customerReviews = reviewRepo.findByUserId(customerId, pageable);

        List<CustomerBeerExperienceDTO.BeerReviewDTO> beerReviewDetails = customerReviews.getContent().stream().map(review -> {
            Beer beer = review.getBeer();
            Brewery brewery = beer.getBrewery();

            BigDecimal abvValue = Optional.ofNullable(beer.getAbv())
                    .map(BigDecimal::valueOf)
                    .orElse(BigDecimal.ZERO);

            return new CustomerBeerExperienceDTO.BeerReviewDTO(
                    beer.getName(),
                    beer.getStyle().getStyle_name(),
                    abvValue.setScale(2, BigDecimal.ROUND_HALF_UP),
                    beer.getCategory().getCat_name(),
                    brewery.getName(),
                    brewery.getCountry(),
                    brewery.getState(),
                    review.getRating(),
                    review.getComment(),
                    review.getReviewDate().toString()
            );
        }).collect(Collectors.toList());

        CustomerBeerExperienceDTO customerExperience = new CustomerBeerExperienceDTO(
                customer.getFirstName() + " " + customer.getLastName(),
                customer.getEmail(),
                beerReviewDetails
        );

        log.info("Customer experience summary successfully fetched for customer ID: {}", customerId);

        return new PageImpl<>(List.of(customerExperience), pageable, customerReviews.getTotalElements());
    }

    // Converts the customer experience data into a TSV (Tab-Separated Values) format.
    public String toTSVFormat(CustomerBeerExperienceDTO experience) {
        StringBuilder tsvBuilder = new StringBuilder();

        tsvBuilder.append("Customer Name\tCustomer Email\tBeer Name\tStyle\tABV\tCategory\tBrewery\tCountry\tState\tRating\tComment\tReview Date\n");

        // Iterate through each beer review and format it for TSV output
        for (CustomerBeerExperienceDTO.BeerReviewDTO reviewDetail : experience.getBeerReviews()) {
            String cleanedComment = reviewDetail.getComment()
                    .replace("\n", " ")
                    .replace("\r", " ")
                    .replaceAll("\\s{2,}", " ")
                    .trim();

            // Format and append each review as a new row in the TSV
            tsvBuilder.append(String.format(
                    "%s\t%s\t%s\t%s\t%.2f\t%s\t%s\t%s\t%s\t%d\t%s\t%s\n",
                    experience.getName(),
                    experience.getEmail(),
                    reviewDetail.getBeerName(),
                    reviewDetail.getBeerStyle(),
                    reviewDetail.getAbv(),
                    reviewDetail.getCategory(),
                    reviewDetail.getBreweryName(),
                    reviewDetail.getCountry(),
                    reviewDetail.getState(),
                    reviewDetail.getRating(),
                    cleanedComment,
                    reviewDetail.getReviewDate()
            ));
        }

        return tsvBuilder.toString();
    }
}
