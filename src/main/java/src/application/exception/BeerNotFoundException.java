package src.application.exception;

public class BeerNotFoundException extends RuntimeException {
    // Exception message
    public BeerNotFoundException(String message) {
        super(message);
    }
}
