package org.example.validation.validators;

import org.example.validation.constraints.NotEmptyString;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;

public class NotEmptyStringValidator
        implements ConstraintValidator<NotEmptyString, String>
{
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return Objects.isNull(value) || !value.isEmpty();
    }
}
