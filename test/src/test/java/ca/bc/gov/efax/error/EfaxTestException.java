package ca.bc.gov.efax.error;

public class EfaxTestException extends RuntimeException {

    public EfaxTestException(String message) {
        super(message);
    }

    public EfaxTestException(String message, Throwable cause) {
        super(message, cause);
    }
}
