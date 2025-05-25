package src.application.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.Max;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import src.application.DTO.ReviewResponse;
import src.application.DTO.ReviewUpdateRequest;
import src.application.model.Review;
import src.application.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reviews")
@Validated
@Tag(name = "Review API", description = "Tasks related to reviews")
@SecurityRequirement(name = "BearerAuth")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // Submits a review to the database
    @Operation(summary = "Create a new review", description = "Allows a customer to submit a new review for a beer")
    @PostMapping
    public ResponseEntity<ReviewResponse> submitReview(
            @RequestParam @NotNull Long customerId,
            @RequestParam @NotNull Long beerId,
            @RequestParam @Min(1) @Max(5) @NotNull Integer rating,
            @RequestParam @NotBlank String comment) {

        Review review = reviewService.submitReview(customerId, beerId, rating, comment);

        if (review == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(ReviewResponse.fromEntity(review));
    }

    // Updates pre-existing review
    @Operation(summary = "Update an existing review", description = "Allows a customer to update an existing review by review ID")
    @PutMapping("{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(
            @Parameter(description = "ID of the review to update", required = true)
            @PathVariable @NotNull Long reviewId,

            @Parameter(description = "New review data", required = true)
            @RequestBody ReviewUpdateRequest request) {

        Review updatedReview = reviewService.updateReview(reviewId, request.getNewRating(), request.getNewComment());

        if (updatedReview == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        ReviewResponse response = ReviewResponse.fromEntity(updatedReview);
        return ResponseEntity.ok(response);
    }

    // Deletes review
//    @Operation(summary = "Delete a review", description = "Allows a customer to delete a review by review ID")
//    @DeleteMapping("{reviewId}")
//    public ResponseEntity<String> deleteReview(
//            @Parameter(description = "ID of the review to delete", required = true)
//            @PathVariable @NotNull Long reviewId) {
//
//        Review deletedReview = reviewService.deleteReview(reviewId);
//
//        if (deletedReview == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Review not found");
//        }
//
//        return ResponseEntity.ok("Review deleted successfully");
//    }

    // Archives review based on reviewId
    @Operation(summary = "Archive a review", description = "Allows a customer to archive a review by review ID")
    @DeleteMapping("{reviewId}")
    public ResponseEntity<String> archiveReview(
            @Parameter(description = "ID of the review to archive", required = true)
            @PathVariable @NotNull Long reviewId) {

        boolean isArchived = reviewService.archiveReview(reviewId);

        if (!isArchived) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Review not found");
        }

        return ResponseEntity.ok("Review archived successfully");
    }

    // Retrieves reviews based on beerId
    @Operation(summary = "Get reviews by beer ID", description = "Retrieve all reviews for a specific beer")
    @GetMapping("/beer/{beerId}")
    public ResponseEntity<Page<ReviewResponse>> getReviewsByBeer(
            @Parameter(description = "ID of the beer", required = true)
            @PathVariable Long beerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviews = reviewService.getReviewsByBeer(beerId, pageable);

        if (reviews.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Page<ReviewResponse> response = reviews.map(ReviewResponse::fromEntity);
        return ResponseEntity.ok(response);
    }

    // Retrieves reviews based on userId
    @Operation(summary = "Get reviews by user ID", description = "Retrieve all reviews written by a specific user")
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ReviewResponse>> getReviewsByUser(
            @Parameter(description = "ID of the user", required = true)
            @PathVariable Long userId,
            Pageable pageable) {

        Page<Review> reviews = reviewService.getReviewsByUser(userId, pageable);

        if (reviews.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Page<ReviewResponse> response = reviews.map(ReviewResponse::fromEntity);

        return ResponseEntity.ok(response);
    }
}
