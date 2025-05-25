package src.application.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BeerWishListItemResponse {
    private Long id;
    private BeerInfo beer;
    private String status;
    private LocalDateTime addedAt;
    private Long userId;
    private Integer rating;
}
