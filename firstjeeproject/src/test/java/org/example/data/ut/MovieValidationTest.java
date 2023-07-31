package org.example.data.ut;

import org.example.data.Movie;

import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class MovieValidationTest {

    static Validator validator;
    static Movie defaultMovie;
    static Movie movieTitleNull;
    static Movie movieTitleBlankSpace;
    static Movie movieTitleBlankTab;
    static Movie movieTitleBlankEol;
    static Movie movieTitleBlanks;
    static Movie movieDefaultYear;
    static Movie movieNotValidYearMedian;
    static Movie movieNotValidYearMax;

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

    @BeforeAll
    static void initData() {
        defaultMovie = new Movie();
        movieTitleNull = Movie.builder()
                .year(2023)
                .build();
        movieTitleBlankSpace = Movie.builder()
                .title(" ")
                .year(2023)
                .build();
        movieTitleBlankTab= Movie.builder()
                .title("\t")
                .year(2023)
                .build();
        movieTitleBlankEol= Movie.builder()
                .title("\n")
                .year(2023)
                .build();
        movieTitleBlanks= Movie.builder()
                .title("  \t\t  \t \r\n  \t  \n  ")
                .year(2023)
                .build();
        movieDefaultYear= Movie.builder()
                .title("Talk to Me")
                .build();
        movieNotValidYearMedian= Movie.builder()
                .title("Talk to Me")
                .year(1000)
                .build();
        movieNotValidYearMax= Movie.builder()
                .title("Talk to Me")
                .year(1887)
                .build();
    }

    static Stream<Movie> notValidMovies(){
        return Stream.of(
                defaultMovie,
                movieTitleNull,
                movieTitleBlankSpace,
                movieTitleBlankTab,
                movieTitleBlankEol,
                movieTitleBlanks,
                movieDefaultYear,
                movieNotValidYearMedian,
                movieNotValidYearMax
        );
    }

    static Stream<Movie> moviesTitleNotValid(){
        return Stream.of(
                defaultMovie,
                movieTitleNull,
                movieTitleBlankSpace,
                movieTitleBlankTab,
                movieTitleBlankEol,
                movieTitleBlanks
        );
    }

    @ParameterizedTest
    @MethodSource("notValidMovies")
    void testNotValidMovieIsOkWithoutValidation(Movie movieNotValid){
    }

    @ParameterizedTest
    @MethodSource("moviesTitleNotValid")
    void testMovieTitleNotValid(Movie movieTitleNotValid){
        // Set<ConstraintViolation<Movie>>
        var violations = validator.validate(movieTitleNotValid);
        // assert Not Valid
        // System.out.println(violations);
        assertTrue(violations.size() > 0, "at least one constraint violation");
        var optTitleNotBlankViolation = violations.stream()
                .filter(v -> "{javax.validation.constraints.NotBlank.message}".equals(v.getMessageTemplate())
                        && "title".equals(v.getPropertyPath().toString()))
                .findFirst();
        assertTrue(optTitleNotBlankViolation.isPresent(), "NotBlank constraint violation on property title");
        var violation = optTitleNotBlankViolation.get();
        //        System.out.println("Message: " + violation.getMessage());
        //        System.out.println("Message template: " + violation.getMessageTemplate());
        //        System.out.println("Property Path: " + violation.getPropertyPath());
        //        System.out.println("Invalid value: " + violation.getInvalidValue());
        assertEquals(movieTitleNotValid.getTitle(), violation.getInvalidValue(), "invalid value");
    }

    @ParameterizedTest
    @ValueSource(strings={
            "Z",
            "Talk to Me",
            "Night of the Day of the Dawn of the Son of the Bride of the Return of the Revenge of the Terror of the Attack of the Evil Mutant Hellbound Flesh Eating Crawling Alien Zombified Subhumanoid Living Dead, Part 5"
    })
    void testMovieTitleValid(String title) {
        var movie = Movie.builder()
                .title(title)
                .year(2023)
                .build();
        var violations = validator.validate(movie);
        assertTrue(violations.isEmpty(), "no violations");
    }

    // TODO: invalidate movie with invalid year
    // TODO: validate movie with valid year

}