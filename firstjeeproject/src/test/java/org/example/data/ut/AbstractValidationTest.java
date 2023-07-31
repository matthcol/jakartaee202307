package org.example.data.ut;

import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.junit.jupiter.api.BeforeAll;

import javax.validation.Validation;
import javax.validation.Validator;

public class AbstractValidationTest {
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
}
