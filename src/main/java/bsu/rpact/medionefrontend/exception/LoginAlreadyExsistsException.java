package bsu.rpact.medionefrontend.exception;

public class LoginAlreadyExsistsException extends RuntimeException {
    public LoginAlreadyExsistsException(String message) {
        super(message);
    }
}
