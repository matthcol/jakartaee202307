package org.example.data.ut;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import org.example.data.Person;
import org.example.validation.constraints.NotEmptyString;
import org.junit.jupiter.api.Test;
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
        assertValid(person);
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
        assertConstraintViolated(person, NotBlank.class, "name", name);
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
        assertValid(person);
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
        assertConstraintViolated(person, PastOrPresent.class, "birthdate", birthdate);
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
        assertValid(person);
    }

    @ParameterizedTest
    @ValueSource(strings={
            // "",  // not handled buy Email constraint
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
        assertConstraintViolated(person, Email.class, "email", email);
    }

    @Test
    void testPersonInvalidEmailEmptyString(){
        String email = "";
        var person = Person.builder()
                .name("Alfred Hitchcock")
                .email(email)
                .build();
        assertConstraintViolated(person, NotEmptyString.class, "email", email);
    }

    // telephone validation

    @ParameterizedTest
    @ValueSource(strings={
            "+33 765 432 109",
            "+33-765-432-109",
            "+33-7-65-43-21-09",
            "+33.765.432.109",
            "+33.7.65.43.21.09",
    })
    @NullSource
    void testPersonValidTelephone(String telephone){
        var person = Person.builder()
                .name("Alfred Hitchcock")
                .telephone(telephone)
                .build();
        assertValid(person);
    }

    @ParameterizedTest
    @ValueSource(strings={
            "",
            "1",
            "a",
            "11111"
    })
    void testPersonInvalidTelephone(String telephone){
        var person = Person.builder()
                .name("Alfred Hitchcock")
                .telephone(telephone)
                .build();
        assertConstraintViolated(person, Pattern.class, "telephone", telephone);
    }

}