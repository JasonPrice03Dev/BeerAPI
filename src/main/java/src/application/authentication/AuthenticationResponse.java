package src.application.authentication;

import lombok.Data;

// Data class containing Tokens for authorization
@Data
public class AuthenticationResponse {
//  private final String jwt;
    private final String accessToken;

    private final String refreshToken;
}
