package org.example.validation.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.OverridesAttribute;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraintvalidation.SupportedValidationTarget;
import jakarta.validation.constraintvalidation.ValidationTarget;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static jakarta.validation.constraintvalidation.ValidationTarget.ANNOTATED_ELEMENT;

/**
 * Custom constraint
 */
@Documented
@Constraint(validatedBy={})
@SupportedValidationTarget(ANNOTATED_ELEMENT)
@Target({METHOD,FIELD,ANNOTATION_TYPE,CONSTRUCTOR,PARAMETER,TYPE_USE})
@Retention(RUNTIME)
@Repeatable(Range.List.class) // need to define internal annotation Range.List
@Min(0L)
@Max(Long.MAX_VALUE)
@ReportAsSingleViolation
public @interface Range {

    @OverridesAttribute(constraint = Min.class, name = "value")
    long min() default 0;

    @OverridesAttribute(constraint = Max.class, name = "value")
    long max() default Long.MAX_VALUE;

    // String message() default "{org.example.validation.constraints.Range.message}"; // need a specific interpolator
    String message() default "must be in range [{min}-{max}]"; // ok with default interpolator
    // String message() default "${validatedValue} must be in range [{min}-{max}]"; // ok with EL

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    /**
     * Defines several {@code @Range} annotations on the same element.
     */
    @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
    @Retention(RUNTIME)
    @Documented
    public @interface List {
        Range[] value();
    }
}
