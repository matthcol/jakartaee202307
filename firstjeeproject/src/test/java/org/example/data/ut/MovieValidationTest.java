package org.example.data.ut;

import org.example.data.Movie;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.stream.Stream;

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
        var validatorFactory = Validation.buildDefaultValidatorFactory();
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
        var constraints = validator.validate(movieTitleNotValid);
        // assert Not Valid
        System.out.println(constraints);
    }

}