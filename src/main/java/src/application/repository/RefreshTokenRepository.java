package src.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import src.application.model.RefreshToken;
import src.application.model.User;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByCustomer(User customer);
}
