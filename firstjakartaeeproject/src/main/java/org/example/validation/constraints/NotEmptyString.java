package org.example.validation.constraints;


import org.example.validation.validators.NotEmptyStringValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


// Java Bean Validation
@Constraint(validatedBy=NotEmptyStringValidator.class)
// Java SE annotation
@Documented
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE, CONSTRUCTOR, TYPE_USE})
@Retention(RUNTIME)
public @interface NotEmptyString {
    String message() default "must not be an empty string";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
