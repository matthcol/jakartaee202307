package org.example.data.ut;

import org.example.data.Person;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class PersonValidationTest extends AbstractValidationTest {

    @ParameterizedTest
    @ValueSource(strings={
            "M",
            "Alfred Hitchcock"
    })
    void testPersonValidName(String name){
        var person = Person.builder()
                .name(name)
                .build();
        var violations = validator.validate(person);
        assertTrue(violations.isEmpty(), "no violations");
    }

    @ParameterizedTest
    @ValueSource(strings={
            "",
            " ",
            "\t",
            "\r",
            "\n",
            "  \t\t  \r\n  \t \n  "
    })
    @NullSource
    void testPersonInvalidName(String name){
        var person = Person.builder()
                .name(name)
                .build();
        var violations = validator.validate(person);
        var optNameNotBlankViolation = violations.stream()
                .filter(v -> "{javax.validation.constraints.NotBlank.message}".equals(v.getMessageTemplate())
                        && "name".equals(v.getPropertyPath().toString()))
                .findFirst();
        assertTrue(optNameNotBlankViolation.isPresent(), "NotBlank constraint violation on property name");
        var violation = optNameNotBlankViolation.get();
        assertEquals(person.getName(), violation.getInvalidValue(), "invalid value");
    }

    static Stream<LocalDate> validBirthdates(){
        return Stream.of(
                LocalDate.of(1900,1,1),
                LocalDate.of(2000,2,29),
                LocalDate.now()
        );
    }

    @ParameterizedTest
    @MethodSource("validBirthdates")
    @NullSource
    void testPersonValidBirthdate(LocalDate birthdate){
        var person = Person.builder()
                .name("Alfred Hitchcock")
                .birthdate(birthdate)
                .build();
        var violations = validator.validate(person);
        assertTrue(violations.isEmpty(), "no violations");
    }

    static Stream<LocalDate> invalidBirthdates(){
        return Stream.of(
                LocalDate.now().plusDays(1L),
                LocalDate.MAX
        );
    }

    @ParameterizedTest
    @MethodSource("invalidBirthdates")
    void testPersonInvalidBirthdate(LocalDate birthdate){
        var person = Person.builder()
                .name("Alfred Hitchcock")
                .birthdate(birthdate)
                .build();
        var violations = validator.validate(person);
        var optBirthdatePasOrPresentViolation = violations.stream()
                .filter(v -> "{javax.validation.constraints.PastOrPresent.message}".equals(v.getMessageTemplate())
                        && "birthdate".equals(v.getPropertyPath().toString()))
                .findFirst();
        assertTrue(optBirthdatePasOrPresentViolation.isPresent(), "Past or present constraint violation on property birthdate");
        var violation = optBirthdatePasOrPresentViolation.get();
        assertEquals(person.getBirthdate(), violation.getInvalidValue(), "invalid value");
    }

    @ParameterizedTest
    @ValueSource(strings={
            "m@spy.org",
            "alfred.hitchcock@directors.org"
    })
    @NullSource
    void testPersonValidEmail(String email){
        var person = Person.builder()
                .name("Alfred Hitchcock")
                .email(email)
                .build();
        var violations = validator.validate(person);
        assertTrue(violations.isEmpty(), "no violations");
    }

    @ParameterizedTest
    @ValueSource(strings={
            "",  // bug
            " ",
            "m",
            "spy.org",
            "m_at_spy.org",
            "m@spy@org"
    })
    void testPersonInvalidEmail(String email){
        var person = Person.builder()
                .name("Alfred Hitchcock")
                .email(email)
                .build();
        var violations = validator.validate(person);
        System.out.println(violations);
        var optEmailViolation = violations.stream()
                .filter(v -> "{javax.validation.constraints.Email.message}".equals(v.getMessageTemplate())
                        && "email".equals(v.getPropertyPath().toString()))
                .findFirst();
        assertTrue(optEmailViolation.isPresent(), "Email constraint violation on property email");
        var violation = optEmailViolation.get();
        assertEquals(person.getEmail(), violation.getInvalidValue(), "invalid value");
    }


}