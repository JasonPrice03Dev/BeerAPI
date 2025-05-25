package src.application.exception;

public class BreweryNotFoundException extends RuntimeException {
    // Exception message
    public BreweryNotFoundException(String message) {
        super(message);
    }
}
