package org.example.data.ut;

import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.junit.jupiter.api.BeforeAll;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

import java.lang.annotation.Annotation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class AbstractValidationTest {
    static Validator validator;

    @BeforeAll
    static void initValidator(){
        // Sol1. Default factory need EL dependency
        // var validatorFactory = Validation.buildDefaultValidatorFactory();
        // Sol2. To avoid that, configure provider with the following lines
        var validatorFactory =
                Validation.byDefaultProvider()
                        .configure()
                        .messageInterpolator(new ParameterMessageInterpolator())
                        .buildValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    static <T, U> void assertConstraintViolated(
            T bean,
            Class<? extends Annotation> constraintAnnotationType,
            String propertyPathStr,
            U valueFact
    ){
        var violations = validator.validate(bean);
        // assert Not Valid
        System.out.println(violations); // Comment this when all tests are ok (debug)
        assertTrue(violations.size() > 0, "at least one constraint violation");
        var optTitleNotBlankViolation = violations.stream()
                .filter(v -> (v.getConstraintDescriptor().getAnnotation().annotationType() == constraintAnnotationType)
                        && propertyPathStr.equals(v.getPropertyPath().toString()))
                .findFirst();
        assertTrue(optTitleNotBlankViolation.isPresent(),
                constraintAnnotationType.getSimpleName()
                        + " constraint violation on property "
                        + propertyPathStr);
        var violation = optTitleNotBlankViolation.get();
        //        System.out.println("Message: " + violation.getMessage());
        //        System.out.println("Message template: " + violation.getMessageTemplate());
        //        System.out.println("Property Path: " + violation.getPropertyPath());
        //        System.out.println("Invalid value: " + violation.getInvalidValue());
        //        System.out.println("Constraint annotation: " +violation.getConstraintDescriptor().getAnnotation());
        //        System.out.println("Constraint annotation type: "
        //                + violation.getConstraintDescriptor().getAnnotation().annotationType());
        assertEquals(valueFact, violation.getInvalidValue(), "invalid value");
    }


}
