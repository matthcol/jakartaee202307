package org.example.data.ut;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
        assertValid(movie);
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
        assertValid(movie);
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
        assertConstraintViolated(movie, Max.class, "duration", duration);
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
        assertValid(movie);
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
        assertConstraintViolated(movie, Range.class, "duration2", duration);
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
        assertValid(movie);
    }

    // genres validation

    @Test
    void testMovieGenresNotValidNull(){
        var movie = Movie.builder()
                .title("Talk to Me")
                .year(2022)
                .genres(null)
                .build();
        assertConstraintViolated(movie, NotNull.class, "genres", null);
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
        assertValid(movie);
    }
}