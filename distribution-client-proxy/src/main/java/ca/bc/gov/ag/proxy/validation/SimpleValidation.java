package ca.bc.gov.ag.proxy.validation;

import java.util.function.Predicate;

public class SimpleValidation<T> implements Validation<T> {

    private final Predicate<T> predicate;
    private final String onErrorMessage;

    public SimpleValidation(Predicate<T> predicate, String onErrorMessage) {
        this.predicate = predicate;
        this.onErrorMessage = onErrorMessage;
    }

    public static <T> SimpleValidation<T> from(Predicate<T> predicate, String onErrorMessage){
        return new SimpleValidation<>(predicate, onErrorMessage);
    }

    @Override
    public ValidationResult test(T param) {
        return predicate.test(param) ? ValidationResult.ok() : ValidationResult.fail(onErrorMessage);
    }
}
