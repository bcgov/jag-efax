package ca.bc.gov.ag.proxy.validation;

public class IntegerValidationHelper {
    public static Validation<Integer> notEqualTo(int value) {
        return SimpleValidation.from(i -> i != value, "value cannot be equal to " + value);
    }

    public static Validation<Integer> greaterThanOrEqualTo(int value) {
        return SimpleValidation.from(i -> i >= value, "value lesser than " + value);
    }
}
