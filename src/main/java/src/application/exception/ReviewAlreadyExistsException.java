package src.application.exception;

public class ReviewAlreadyExistsException extends RuntimeException {
    // Exception message
    public ReviewAlreadyExistsException(String message) {
        super(message);
    }
}
