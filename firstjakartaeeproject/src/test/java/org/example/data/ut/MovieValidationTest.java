package org.example.data.ut;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.example.data.Movie;

import org.example.validation.constraints.Range;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;


import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class MovieValidationTest extends AbstractValidationTest {
    static Movie defaultMovie;
    static Movie movieTitleNull;

    static Movie movieTitleBlankEmpty;
    static Movie movieTitleBlankSpace;
    static Movie movieTitleBlankTab;
    static Movie movieTitleBlankEol;
    static Movie movieTitleBlanks;
    static Movie movieDefaultYear;
    static Movie movieNotValidYearMedian;
    static Movie movieNotValidYearMax;


    @BeforeAll
    static void initData() {
        defaultMovie = new Movie();
        movieTitleNull = Movie.builder()
                .year(2023)
                .build();
        movieTitleBlankEmpty = Movie.builder()
                .title("")
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
                movieTitleBlankEmpty,
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
                movieTitleBlankEmpty,
                movieTitleBlankSpace,
                movieTitleBlankTab,
                movieTitleBlankEol,
                movieTitleBlanks
        );
    }

    static Stream<Movie> moviesYearNotValid(){
        return Stream.of(
                defaultMovie,
                movieDefaultYear,
                movieNotValidYearMedian,
                movieNotValidYearMax
        );
    }

    @ParameterizedTest
    @MethodSource("notValidMovies")
    void testNotValidMovieIsOkWithoutValidation(Movie movieNotValid){
    }

    @ParameterizedTest
    @MethodSource("moviesTitleNotValid")
    void testMovieTitleNotValid(Movie movieTitleNotValid){
        assertConstraintViolated(movieTitleNotValid, NotBlank.class, "title", movieTitleNotValid.getTitle());
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

    @ParameterizedTest
    @MethodSource("moviesYearNotValid")
    void testMovieYearNotValid(Movie movieYearNotValid){
        assertConstraintViolated(movieYearNotValid, Min.class, "year", movieYearNotValid.getYear());
    }

    @ParameterizedTest
    @ValueSource(ints={
            1888,
            1923,
            Integer.MAX_VALUE
    })
    void testMovieYearValid(int year) {
        var movie = Movie.builder()
                .title("Talk to Me")
                .year(year)
                .build();
        var violations = validator.validate(movie);
        assertTrue(violations.isEmpty(), "no violations");
    }

    // duration with @Min, @Max constraints

    @ParameterizedTest
    @ValueSource(ints={
            0,
            10,
            39,
    })
    void testMovieDurationNotValidMin(Integer duration) {
        var movie = Movie.builder()
                .title("Talk to Me")
                .year(2022)
                .duration(duration)
                .build();
        assertConstraintViolated(movie, Min.class, "duration", duration);
    }

    @ParameterizedTest
    @ValueSource(ints={
            301,
            1440,
            Integer.MAX_VALUE,
    })
    void testMovieDurationNotValidMax(Integer duration) {
        var movie = Movie.builder()
                .title("Talk to Me")
                .year(2022)
                .duration(duration)
                .build();
        var violations = validator.validate(movie);
        System.out.println(violations);
        var optDurationMinViolation = violations.stream()
                .filter(v -> "{javax.validation.constraints.Max.message}".equals(v.getMessageTemplate())
                        && "duration".equals(v.getPropertyPath().toString()))
                .findFirst();
        assertTrue(optDurationMinViolation.isPresent(), "Max violation on property duration");
        var violation = optDurationMinViolation.get();
        assertEquals(duration, violation.getInvalidValue(), "invalid value");
    }

    @ParameterizedTest
    @ValueSource(ints={
            40,
            120,
            300,
    })
    @NullSource
    void testMovieDurationValid(Integer duration) {
        var movie = Movie.builder()
                .title("Talk to Me")
                .year(2022)
                .duration(duration)
                .build();
        var violations = validator.validate(movie);
        assertTrue(violations.isEmpty(), "no violations");
    }

    // duration2 with @Range constraints

    @ParameterizedTest
    @ValueSource(ints={
            0,
            10,
            39,
            301,
            1440,
            Integer.MAX_VALUE,
    })
    void testMovieDuration2NotValidRange(Integer duration) {
        var movie = Movie.builder()
                .title("Talk to Me")
                .year(2022)
                .duration2(duration)
                .build();
        var violations = validator.validate(movie);
        System.out.println(violations);
        var optDurationRangeViolation = violations.stream()
                .filter(v -> (v.getConstraintDescriptor().getAnnotation().annotationType() == Range.class)
                        && "duration2".equals(v.getPropertyPath().toString()))
                .findFirst();

        assertTrue(optDurationRangeViolation.isPresent(), "Range violation on property duration");
        var violation = optDurationRangeViolation.get();
        //        System.out.println("Message: " + violation.getMessage());
        //        System.out.println("Message template: " + violation.getMessageTemplate());
        //        System.out.println("Property Path: " + violation.getPropertyPath());
        //        System.out.println("Invalid value: " + violation.getInvalidValue());
        //        System.out.println("Constraint annotation: " +violation.getConstraintDescriptor().getAnnotation());
        //        System.out.println("Constraint annotation type: "
        //                + violation.getConstraintDescriptor().getAnnotation().annotationType());
        assertEquals(duration, violation.getInvalidValue(), "invalid value");
    }

    @ParameterizedTest
    @ValueSource(ints={
            40,
            120,
            300,
    })
    @NullSource
    void testMovieDuration2Valid(Integer duration) {
        var movie = Movie.builder()
                .title("Talk to Me")
                .year(2022)
                .duration2(duration)
                .build();
        var violations = validator.validate(movie);
        assertTrue(violations.isEmpty(), "no violations");
    }

    // genres validation

    @Test
    void testMovieGenresNotValidNull(){
        var movie = Movie.builder()
                .title("Talk to Me")
                .year(2022)
                .genres(null)
                .build();
        var violations = validator.validate(movie);
        //  System.out.println(violations);
        var optGenresNotNullViolation = violations.stream()
                .filter(v -> "{javax.validation.constraints.NotNull.message}".equals(v.getMessageTemplate())
                        && "genres".equals(v.getPropertyPath().toString()))
                .findFirst();
        assertTrue(optGenresNotNullViolation.isPresent(), "Not null violation on property genres");
        var violation = optGenresNotNullViolation.get();
        assertNull(violation.getInvalidValue(), "invalid null value");
    }

    static Stream<Movie> moviesGenresValid(){
        return Stream.of(
                Movie.builder() // default genres
                        .title("Talk to Me")
                        .year(2022)
                        .build(),
                Movie.builder() // explicit empty set
                        .title("Talk to Me")
                        .year(2022)
                        .genres(Set.of())
                        .build(),
                Movie.builder()
                        .title("Talk to Me")
                        .year(2022)
                        .genres(Set.of("Horror"))
                        .build(),
                Movie.builder()
                        .title("Talk to Me")
                        .year(2022)
                        .genres(Set.of("Horror", "Thriller"))
                        .build()
        );
    }

    @ParameterizedTest
    @MethodSource("moviesGenresValid")
    void testMovieGenresValid(Movie movie){
        var violations = validator.validate(movie);
        assertTrue(violations.isEmpty(), "no violations");
    }
}