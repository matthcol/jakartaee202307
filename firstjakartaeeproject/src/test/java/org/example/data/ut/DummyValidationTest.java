package org.example.data.ut;

import org.example.data.Dummy;
import org.example.data.Person;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TODO: rewrite this class with individual unit tests
 */
class DummyValidationTest extends AbstractValidationTest {

    @Test
    void testDummyNotValid() {
        var dummy = Dummy.builder()
                .textMinSized("aaaa")
                .textMaxSized("aaaaaaaaaaa")
                .textSized("b")
                .textsSized(List.of())
                .price(BigDecimal.valueOf(329, 2))
                .person(Person.builder()
                        .name("\t\t")
                        .birthdate(LocalDate.now().plusDays(7L))
                        .build())
                .persons1(List.of(
                        Person.builder()
                                .build(),
                        Person.builder()
                                .name("John")
                                .birthdate(LocalDate.now().plusDays(7L))
                                .build()
                ))
                .persons2(List.of(
                        Person.builder()
                                .build(),
                        Person.builder()
                                .name("\n")
                                .birthdate(LocalDate.now().plusDays(7L))
                                .build(),
                        Person.builder()
                                .name("John")
                                .birthdate(LocalDate.now().plusDays(7L))
                                .build()
                ))
                .listTextSized(List.of("H", "Ho", "Hor"))
                .temperatures(Set.of(12, 5, 25, 4))
                .indexWords(Map.of(
                        "jakarta", 2,
                        "java", 0,
                        "\t\n ", 1,
                        "to", 10
                ))
                .build();
        var violations = validator.validate(dummy);
        violations.forEach(AbstractValidationTest::reportViolation);
    }
}