package src.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import src.application.authentication.AuthenticationRequest;
import src.application.authentication.AuthenticationResponse;
import src.application.config.JwtUtil;
import src.application.service.RefreshTokenService;
import src.application.service.TokenBlacklistService;

import java.util.Map;

@RestController
@Tag(name = "Authentication", description = "Tasks related to authentication")
@SecurityRequirement(name = "BearerAuth")  // Applying security for Swagger
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    // Authenticates a user from email and password in order to generate a Token
    @Operation(
            summary = "Authenticate user and generates token",
            description = "Authenticates a user using email and password to generate an access token and refresh token",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful Authentication", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = AuthenticationResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Invalid credentials"),
                    @ApiResponse(responseCode = "500", description = "Failed to generate token")
            }
    )
    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect username or password");
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());

        tokenBlacklistService.blacklistPreviousTokens(userDetails.getUsername());

        final String jwt = jwtTokenUtil.generateToken(userDetails.getUsername());

        String accessToken = jwtTokenUtil.generateToken(userDetails.getUsername());

        tokenBlacklistService.storeToken(userDetails.getUsername(), accessToken);

        String refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());

        if (jwt == null || jwt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to generate token");
        }

        return ResponseEntity.ok(new AuthenticationResponse(accessToken, refreshToken));
    }

    // Generates new Access Token
    @Operation(
            summary = "Generates new access token",
            description = "Generates a new access token using the provided refresh token",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully refreshed access token", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = Map.class))),
                    @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
            }
    )
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshAccessToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        if (refreshToken == null || !refreshTokenService.isValidRefreshToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired refresh token");
        }

        String username = refreshTokenService.getUsernameFromToken(refreshToken);

        tokenBlacklistService.blacklistPreviousTokens(username);

        String newAccessToken = jwtTokenUtil.generateToken(username);

        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    // Logs user out (Blacklisting tokens)
    @Operation(
            summary = "Logout user out and blacklist token",
            description = "Logs the user out and blacklists the provided access and refresh tokens",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully logged out"),
                    @ApiResponse(responseCode = "400", description = "Authorization header is required for logout")
            }
    )
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @Parameter(description = "Access token of the user") @RequestHeader("Authorization") String accessToken,
            @RequestBody Map<String, String> request) {

        String refreshToken = request.get("refreshToken");

        if (accessToken == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Authorization header is required for logout");
        }

        if (refreshToken != null) {
            refreshTokenService.revokeRefreshToken(refreshToken);
        }

        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            tokenBlacklistService.blacklistToken(accessToken.substring(7));
        }

        return ResponseEntity.ok("Logged out successfully");
    }
}
