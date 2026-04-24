package org.example.commerce.validation;

import org.example.commerce.validation.validator.ValidPriceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ValidPriceValidatorTest {
    private ValidPriceValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ValidPriceValidator();
    }

    @Test
    void isValid_multipleOf1000_returnTrue() {
        assertTrue(validator.isValid(BigDecimal.valueOf(25000), null));
    }

    @Test
    void isValid_notMultipleOf1000_returnFalse() {
        assertFalse(validator.isValid(BigDecimal.valueOf(25500), null));
    }

    @Test
    void isValid_null_returnTrue() {
        assertTrue(validator.isValid(null, null));
    }
}
