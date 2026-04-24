package org.example.commerce.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.commerce.validation.annotation.ValidPrice;

import java.math.BigDecimal;

public class ValidPriceValidator implements ConstraintValidator<ValidPrice, BigDecimal> {

    @Override
    public void initialize(ValidPrice constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext constraintValidatorContext) {
        if(value == null) {
            return true;
        }
        return value.remainder(BigDecimal.valueOf(1000)).compareTo(BigDecimal.ZERO) == 0;
    }
}
