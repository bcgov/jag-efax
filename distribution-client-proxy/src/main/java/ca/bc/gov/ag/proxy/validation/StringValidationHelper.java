package ca.bc.gov.ag.proxy.validation;

import java.util.Objects;

public class StringValidationHelper {
    public static Validation<String> notNull = SimpleValidation.from(Objects::nonNull, "string cannot be null");
    public static Validation<String> notEmpty = SimpleValidation.from(str -> !str.isEmpty(), "string cannot be empty");
}
