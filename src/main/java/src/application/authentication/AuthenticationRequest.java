package src.application.authentication;

import lombok.Data;

// Data class to aid user authentication
@Data
public class AuthenticationRequest {
    private String email;
    private String password;
}
