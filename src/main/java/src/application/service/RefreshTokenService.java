package src.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import src.application.model.RefreshToken;
import src.application.model.User;
import src.application.repository.CustomerRepository;
import src.application.repository.RefreshTokenRepository;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private static final long REFRESH_TOKEN_DURATION = 7 * 24 * 60 * 60;

    // Creates new token
    public String createRefreshToken(String email) {
        User customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        String token = UUID.randomUUID().toString();
        Instant expiryDate = Instant.now().plusSeconds(REFRESH_TOKEN_DURATION);

        RefreshToken refreshToken = new RefreshToken(token, expiryDate, customer);
        refreshTokenRepository.save(refreshToken);

        return token;
    }

    // Checks to ensure token is valid
    public boolean isValidRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .filter(rt -> rt.getExpiryDate().isAfter(Instant.now()))
                .isPresent();
    }

    // Retrieves username from token
    public String getUsernameFromToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .map(rt -> rt.getCustomer().getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));
    }

    // Removes refresh token(invalidates)
    public void revokeRefreshToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(refreshTokenRepository::delete);
    }
}
