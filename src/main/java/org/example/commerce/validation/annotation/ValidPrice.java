package org.example.commerce.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.example.commerce.validation.validator.ValidPriceValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidPriceValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPrice {
    String message() default "Price must be multiple of 1000";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
