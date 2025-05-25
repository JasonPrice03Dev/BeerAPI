package src.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import src.application.DTO.BeerInfo;
import src.application.DTO.BeerWishListItemResponse;
import src.application.model.BeerWishListItem;
import src.application.service.BeerWishListService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/wishlist")
@Tag(name = "Beer Wishlist", description = "Tasks related to managing a user's beer wishlist")
@SecurityRequirement(name = "BearerAuth")
public class BeerWishListController {

    @Autowired
    private BeerWishListService beerWishListService;

    // Add a beer to the wishlist
    @Operation(summary = "Add a beer to the user's wishlist", description = "Adds a beer to the user's wishlist based on user ID and beer ID")
    @ApiResponse(responseCode = "201", description = "Beer added to wishlist",
            content = @Content(schema = @Schema(implementation = BeerWishListItemResponse.class)))
    @ApiResponse(responseCode = "404", description = "User or beer not found")
    @PostMapping("/{userId}/beers/{beerId}")
    public ResponseEntity<BeerWishListItemResponse> addBeerToWishlist(
            @Parameter(description = "ID of the user") @PathVariable Long userId,
            @Parameter(description = "ID of the beer") @PathVariable Long beerId) {

        BeerWishListItem addedItem = beerWishListService.addBeerToWishlist(userId, beerId);

        if (addedItem == null) {
            return ResponseEntity.notFound().build();
        }

        BeerWishListItemResponse response = new BeerWishListItemResponse(
                addedItem.getId(),
                new BeerInfo(addedItem.getBeer().getId(), addedItem.getBeer().getName()),
                addedItem.getStatus().name(),
                addedItem.getAddedAt(),
                addedItem.getBeerWishList().getUser().getId(),
                addedItem.getRating()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Remove a beer from the wishlist
//    @Operation(summary = "Remove a beer from the wishlist", description = "Removes a specific beer from the user's wishlist")
//    @ApiResponse(responseCode = "200", description = "Beer removed successfully")
//    @ApiResponse(responseCode = "404", description = "User or beer not found in wishlist")
//    @DeleteMapping("/{userId}/beers/{beerId}")
//    public ResponseEntity<String> removeBeerFromWishlist(
//            @Parameter(description = "ID of the user") @PathVariable Long userId,
//            @Parameter(description = "ID of the beer") @PathVariable Long beerId) {
//
//        Boolean removed = beerWishListService.removeBeerFromWishlist(userId, beerId);
//
//        if (removed == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User or beer not found in wishlist");
//        }
//
//        return ResponseEntity.ok("Beer removed from wishlist");
//    }

    @Operation(summary = "Archive a beer from the wishlist", description = "Archives a specific beer from the user's wishlist instead of deleting it")
    @ApiResponse(responseCode = "200", description = "Beer archived successfully")
    @ApiResponse(responseCode = "404", description = "User or beer not found in wishlist")
    @DeleteMapping("/{userId}/beers/{beerId}")
    public ResponseEntity<String> archiveBeerFromWishlist(
            @Parameter(description = "ID of the user") @PathVariable Long userId,
            @Parameter(description = "ID of the beer") @PathVariable Long beerId) {

        boolean archived = beerWishListService.ArchiveBeerFromWishlist(userId, beerId);

        if (!archived) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User or beer not found in wishlist");
        }

        return ResponseEntity.ok("Beer archived successfully");
    }


    // View the wishlist for a user
    @Operation(summary = "Get user's wishlist", description = "Retrieves the list of beers in a user's wishlist")
    @ApiResponse(responseCode = "200", description = "Wishlist retrieved successfully",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = BeerWishListItemResponse.class))))
    @ApiResponse(responseCode = "404", description = "Wishlist not found")
    @GetMapping("/{userId}/beers")
    public ResponseEntity<Map<String, Object>> getWishlist(
            @Parameter(description = "ID of the user") @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("addedAt").descending());

        Page<BeerWishListItem> wishlistItems = beerWishListService.getWishlist(userId, pageable);

        if (wishlistItems.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<BeerWishListItemResponse> responseList = wishlistItems.stream()
                .map(item -> new BeerWishListItemResponse(
                        item.getId(),
                        new BeerInfo(item.getBeer().getId(), item.getBeer().getName()),
                        item.getStatus().name(),
                        item.getAddedAt(),
                        item.getBeerWishList().getUser().getId(),
                        item.getRating()
                ))
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("beers", responseList);
        response.put("currentPage", wishlistItems.getNumber());
        response.put("totalItems", wishlistItems.getTotalElements());
        response.put("totalPages", wishlistItems.getTotalPages());

        return ResponseEntity.ok(response);
    }

    // Mark a beer as tasted and add a rating
    @Operation(summary = "Mark a beer as tasted", description = "Marks a beer in the user's wishlist as tasted and assigns a rating")
    @ApiResponse(responseCode = "200", description = "Beer marked as tasted and rated",
            content = @Content(schema = @Schema(implementation = BeerWishListItemResponse.class)))
    @ApiResponse(responseCode = "404", description = "User or beer not found")
    @PutMapping("/{userId}/beers/{beerId}/tasted")
    public ResponseEntity<BeerWishListItemResponse> markAsTasted(
            @Parameter(description = "ID of the user") @PathVariable Long userId,
            @Parameter(description = "ID of the beer") @PathVariable Long beerId,
            @Parameter(description = "Rating given by the user") @RequestParam("rating") Integer rating) {

        BeerWishListItem updatedItem = beerWishListService.markAsTasted(userId, beerId, rating);

        if (updatedItem == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        BeerWishListItemResponse response = new BeerWishListItemResponse(
                updatedItem.getId(),
                new BeerInfo(updatedItem.getBeer().getId(), updatedItem.getBeer().getName()),
                updatedItem.getStatus().name(),
                updatedItem.getAddedAt(),
                updatedItem.getBeerWishList().getUser().getId(),
                updatedItem.getRating()
        );

        return ResponseEntity.ok(response);
    }
}