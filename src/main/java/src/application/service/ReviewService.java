package src.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import src.application.exception.BeerNotFoundException;
import src.application.exception.ReviewAlreadyExistsException;
import src.application.model.ArchivedReview;
import src.application.model.Beer;
import src.application.model.Review;
import src.application.model.User;
import src.application.repository.ArchivedReviewRepository;
import src.application.repository.BeerRepository;
import src.application.repository.CustomerRepository;
import src.application.repository.ReviewRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private static final Logger log = LoggerFactory.getLogger(ReviewService.class);

    @Autowired
    private BeerRepository beerRepository;

    @Autowired
    private ArchivedReviewRepository archivedReviewRepository;

    // Submits a new review for a beer by a customer.
    @Transactional
    public Review submitReview(Long customerId, Long beerId, int rating, String comment) {
        User customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) {
            return null;
        }

        Beer beer = beerRepository.findById(beerId).orElse(null);
        if (beer == null) {
            return null;
        }

        if (reviewRepository.findByUserIdAndBeerId(customerId, beerId).isPresent()) {
            throw new ReviewAlreadyExistsException("Customer has already reviewed this beer");
        }

        Review review = new Review();
        review.setUser(customer);
        review.setBeer(beer);
        review.setRating(rating);
        review.setComment(comment);
        review.setReviewDate(new Date());

        Review savedReview = reviewRepository.save(review);
        recalculateBeerRating(beer);

        return savedReview;
    }

    // Updates an existing review
    @Transactional
    public Review updateReview(Long reviewId, int newRating, String newComment) {
        Review review = reviewRepository.findById(reviewId).orElse(null);

        if (review == null) {
            return null;
        }

        review.setRating(newRating);
        review.setComment(newComment);
        review.setReviewDate(new Date());

        Review updatedReview = reviewRepository.save(review);
        recalculateBeerRating(review.getBeer());

        return updatedReview;
    }

    // deletes an existing review
    @Transactional
    public Review deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElse(null);

        if (review == null) {
            return null;
        }

        Beer beer = review.getBeer();
        reviewRepository.delete(review);
        recalculateBeerRating(beer);

        return review;
    }

    // Archives the review rather than out right deleting
    @Transactional
    public boolean archiveReview(Long reviewId) {
        Optional<Review> reviewOpt = reviewRepository.findById(reviewId);
        if (reviewOpt.isEmpty()) {
            return false;
        }

        Review review = reviewOpt.get();

        ArchivedReview archivedReview = new ArchivedReview();
        archivedReview.setOriginalReviewId(review.getId());
        archivedReview.setUser(review.getUser());
        archivedReview.setBeer(review.getBeer());
        archivedReview.setRating(review.getRating());
        archivedReview.setComment(review.getComment());
        archivedReview.setReviewDate(review.getReviewDate());
        archivedReview.setArchivedAt(new Date());

        archivedReviewRepository.save(archivedReview);

        reviewRepository.delete(review);

        return true;
    }

    // Recalculates beer rating based on all reviews
    private void recalculateBeerRating(Beer beer) {
        List<Review> reviews = reviewRepository.findByBeerId(beer.getId());
        double averageRating = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        beer.setAverageRating(averageRating);
        beerRepository.save(beer);
    }

    // Retrieves beer reviews for specific beer
    @Transactional
    public Page<Review> getReviewsByBeer(Long beerId, Pageable pageable) {
        Beer beer = beerRepository.findById(beerId)
                .orElseThrow(() -> new BeerNotFoundException("Beer not found"));

        if (beer == null) {
            log.warn("Beer with ID {} not found", beerId);
            return null;
        }

        return reviewRepository.findByBeerId(beerId, pageable);
    }

    // Retrieves reviews for specific user
    @Transactional
    public Page<Review> getReviewsByUser(Long userId, Pageable pageable) {
        User user = customerRepository.findById(userId).orElse(null);

        if (user == null) {
            log.warn("User with ID {} not found", userId);
            return null;
        }

        return reviewRepository.findByUserId(userId, pageable);
    }
}