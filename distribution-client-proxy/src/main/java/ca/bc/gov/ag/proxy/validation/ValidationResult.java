package ca.bc.gov.ag.proxy.validation;

import javax.validation.ValidationException;

public class ValidationResult {
    private final boolean valid;
    private final String onErrorMessage;

    public ValidationResult(boolean valid) {
        this(valid, "");
    }

    public ValidationResult(boolean valid, String onErrorMessage) {
        this.valid = valid;
        this.onErrorMessage = onErrorMessage;
    }

    public static ValidationResult ok() {
        return new ValidationResult(true, "");
    }

    public static ValidationResult fail(String onErrorMessage) {
        return new ValidationResult(false, onErrorMessage);
    }

    public boolean isValid() {
        return valid;
    }

    public String getOnErrorMessage() {
        return onErrorMessage;
    }

    public void throwIfInvalid(String str) {
        if (!isValid())
            throw new ValidationException("'" + str + "' " + onErrorMessage);
    }
}
