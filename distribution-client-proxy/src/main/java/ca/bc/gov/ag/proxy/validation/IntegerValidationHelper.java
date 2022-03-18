package ca.bc.gov.ag.proxy.validation;

public class IntegerValidationHelper {
    public static Validation<Integer> notEqualTo(int value) {
        return SimpleValidation.from(i -> i != value, "value cannot be equal to " + value);
    }
    public static Validation<Integer> lesserThan(int value) {
        return SimpleValidation.from(i -> value <= i, "value cannot be lesser than " + value);
    }

}
