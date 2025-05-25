package src.application.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import src.application.model.Review;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ReviewResponse {
    private Long id;
    private Long userId;
    private int rating;
    private String comment;
    private Date reviewDate;
    public ReviewResponse(Long id, Long userId, int rating, String comment, Date reviewDate) {
        this.id = id;
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
        this.reviewDate = reviewDate;
    }
    public Date getReviewDate() {
        return reviewDate;
    }
    public static ReviewResponse fromEntity(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getUser().getId(),
                review.getRating(),
                review.getComment(),
                review.getReviewDate()
        );
    }
}
