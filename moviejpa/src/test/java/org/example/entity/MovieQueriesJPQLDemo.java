package org.example.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Tuple;
import org.example.dto.StatisticsByYear;
import org.example.dto.TitleYear;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Optional;

/**
 * Doc:
 * - Hibernate:
 * https://docs.jboss.org/hibernate/orm/6.2/userguide/html_single/Hibernate_User_Guide.html#hql
 * https://docs.jboss.org/hibernate/orm/6.2/userguide/html_single/Hibernate_User_Guide.html#query-language
 * - Jakarta EE JPA:
 * https://jakarta.ee/specifications/persistence/3.1/jakarta-persistence-spec-3.1#a4665
 */
public class MovieQueriesJPQLDemo {

    static EntityManagerFactory entityManagerFactory;
    static EntityManager entityManager;

    @BeforeAll
    static void bootstrapJpa() {
        entityManagerFactory = Persistence.createEntityManagerFactory("DBMariaDBQueries");
        entityManager = entityManagerFactory.createEntityManager();
    }

    @AfterAll
    static void shutdownJpa() {
        entityManager.close();
        entityManagerFactory.close();
    }

    @Test
    void demoFindAll(){
        // NB: N directors are fetched (expensive cost)
        String queryJPQL = "SELECT m FROM Movie m";
        var movies = entityManager.createQuery(queryJPQL, Movie.class)
                .getResultList(); // execute SQL and store results in a List<Movie>
        System.out.println("Movie count: " + movies.size());
        movies.stream()
                .skip(200)
                .limit(10)
                .peek(System.out::println)
                .forEach(movie -> {
                    System.out.println("\t- title = " + movie.getTitle());
                    System.out.println("\t- year = " + movie.getYear());
                });
    }

    @Test
    void demoFindAllWithDirector(){
        // NB: N directors are fetched (expensive cost)
        String queryJPQL = "SELECT m FROM Movie m LEFT JOIN FETCH m.director";
        var movies = entityManager.createQuery(queryJPQL, Movie.class)
                .getResultList(); // execute SQL and store results in a List<Movie>
        System.out.println("Movie count: " + movies.size());
        movies.stream()
                .skip(200)
                .limit(10)
                .peek(System.out::println)
                .forEach(movie -> {
                    System.out.println("\t- title = " + movie.getTitle());
                    System.out.println("\t- year = " + movie.getYear());
                    System.out.println("\t- director = " + movie.getDirector());
                });
    }


    @Test
    void demoFindAllByYear(){
        String queryJPQL = "SELECT m FROM Movie m WHERE m.year = :year";
        short year = 1984;
        entityManager.createQuery(queryJPQL, Movie.class)
                .setParameter("year", year)
                .getResultStream() // execute SQL query
                .forEach(System.out::println);
    }

    @ParameterizedTest
    @CsvSource({
            "1984,Gremlins",
            "1934,The Man Who Knew Too Much",
            "1956,The Man Who Knew Too Much",
            "1984,B%"
    })
    void demoFindAllByYearAndTitle(short year, String title){
        String queryJPQL = """
                SELECT
                    m
                FROM
                    Movie m
                WHERE
                    m.year = :year
                    AND m.title LIKE :title
        """;

        System.out.println(MessageFormat.format(" - Find movies with title like {0} and year = {1} -",
                title, year));
        entityManager.createQuery(queryJPQL, Movie.class)
                .setParameter("year", year)
                .setParameter("title", title)
                .getResultStream() // execute SQL query
                .forEach(System.out::println);
    }

    // EXo: movies with year between year1 and year2 sorted by year, title
    @ParameterizedTest
    @CsvSource({
            "1980,1989",
            "2020,2029"
    })
    void getMoviesBetweenYears(short year1, short year2) {
        String queryJPQL = """
                        SELECT
                            m
                        FROM
                            Movie m
                        WHERE
                            m.year BETWEEN :year1 AND :year2
                        ORDER BY
                            m.year, m.title
                """;

        System.out.println(MessageFormat.format(" - Find movies between years {0} and {1} -",
                year1, year2));
        entityManager.createQuery(queryJPQL, Movie.class)
                .setParameter("year1", year1)
                .setParameter("year2", year2)
                .getResultStream() // execute SQL query
                .forEach(System.out::println);
    }

    // Exo: movies with no duration sorted by title

    @Test
    void getMoviesWithNoDurationSortedByTitle() {
        // NB: predicate IS NULL, IS NOT NULL
        String queryJPQL = """
                SELECT
                    m
                FROM
                    Movie m
                WHERE
                    m.duration IS NULL
                ORDER BY
                    m.title
        """;

        System.out.println(" - Find movies with no duration -");
        entityManager.createQuery(queryJPQL, Movie.class)
                .getResultStream() // execute SQL query
                .forEach(System.out::println);
    }

    // Exo: movies with title length > 100
    @ParameterizedTest
    @ValueSource(ints={300, 200, 100, 50})
    void demoFindMoviesWithTitleLengthGreaterOrEquals(int lengthMin){
        String queryJPQL = """
                SELECT
                    m
                FROM 
                    Movie m
                WHERE
                    LENGTH(m.title) >= :lengthMin
                ORDER BY
                    LENGTH(m.title) DESC
                """;
        System.out.println(MessageFormat.format(
                " - Find movies with title length >= {0} -",
                lengthMin
        ));
        entityManager.createQuery(queryJPQL, Movie.class)
                .setParameter("lengthMin", lengthMin)
                .getResultStream()
                .forEach(System.out::println);
    }

    // Exo: movies from last 10 years sorted by year desc, title asc
    @Test
    void demoFindMoviesFromLast10Years(){
        short countYear = 10;
        String queryJPQL = """
               SELECT
                    m
               FROM 
                    Movie m
               WHERE
                    m.year > EXTRACT(YEAR FROM CURRENT_DATE) - :countYear
               ORDER BY
                    m.year DESC, m.title 
                """;
        System.out.println(MessageFormat.format(
                " - Find movies from {0} last years -",
                countYear
        ));
        entityManager.createQuery(queryJPQL, Movie.class)
                .setParameter("countYear", countYear)
                .getResultStream()
                .forEach(System.out::println);
    }

    @ParameterizedTest
    @ValueSource(strings={
            "%Star%"
    })
    void demoFindTitlesLike(String title){
        String queryJPQL = """
                SELECT
                    m.title
                FROM
                    Movie m
                WHERE
                    m.title like :title
                ORDER BY m.title
                """;
        var titles = entityManager.createQuery(queryJPQL, String.class)
                .setParameter("title", title)
                .getResultList();
        titles.forEach(System.out::println);
    }

    @ParameterizedTest
    @ValueSource(strings={
            "%Star%"
    })
    void demoFindTitleYearWithTitleLikeToObjectArray(String titlePattern){
        String queryJPQL = """
                SELECT
                    m.title,
                    m.year
                FROM
                    Movie m
                WHERE
                    m.title like :titlePattern
                ORDER BY m.title
                """;
        var resultList = entityManager.createQuery(queryJPQL, Object[].class)
                .setParameter("titlePattern", titlePattern)
                .getResultList();
        resultList.forEach(row -> {
            System.out.println(Arrays.toString(row));
            String title = (String) row[0];
            short year = (short) row[1];
            System.out.println("\t- title: " + title);
            System.out.println("\t- year: " + year);
        });
    }

    @ParameterizedTest
    @ValueSource(strings={
            "%Star%"
    })
    void demoFindTitleYearWithTitleLikeToTuple(String titlePattern){
        String queryJPQL = """
                SELECT
                    m.title as title,
                    m.year as year
                FROM
                    Movie m
                WHERE
                    m.title like :titlePattern
                ORDER BY m.title
                """;
        var resultList = entityManager.createQuery(queryJPQL, Tuple.class)
                .setParameter("titlePattern", titlePattern)
                .getResultList();
        resultList.forEach(tuple -> {
            System.out.println(tuple);
            String title = tuple.get("title", String.class);
            short year = tuple.get("year", short.class);
            System.out.println("\t- title: " + title);
            System.out.println("\t- year: " + year);
        });
    }

    @ParameterizedTest
    @ValueSource(strings={
            "%Star%"
    })
    void demoFindTitleYearWithTitleLikeToDto(String titlePattern){
        String queryJPQL = """
                SELECT
                    new org.example.dto.TitleYear(
                        m.title,
                        m.year
                    )
                FROM
                    Movie m
                WHERE
                    m.title like :titlePattern
                ORDER BY m.title
                """;
        var titleYearList = entityManager.createQuery(queryJPQL, TitleYear.class)
                .setParameter("titlePattern", titlePattern)
                .getResultList();
        titleYearList.forEach(titleYear -> {
            System.out.println(titleYear);
            String title = titleYear.getTitle();
            short year = titleYear.getYear();
            System.out.println("\t- title: " + title);
            System.out.println("\t- year: " + year);
        });
    }

    @ParameterizedTest
    @ValueSource(shorts={1984})
    void demoFindMovieTitleUpperCaseDurationHourMinute(short year){
        String queryJPQL = """
                SELECT
                    UPPER(m.title) as title,
                    CAST(duration / 60 AS int) as hour,
                    MOD(duration, 60) as minute
                FROM
                    Movie m
                WHERE
                    m.year = :year
                ORDER BY
                    title
                """;
        entityManager.createQuery(queryJPQL, Tuple.class)
                .setParameter("year", year)
                .getResultStream()
                .forEach(tuple -> System.out.println(MessageFormat.format(
                        "\t- title = {0} ; hour = {1}; minute = {2}",
                        tuple.get("title", String.class),
                        tuple.get("hour", int.class),
                        tuple.get("minute", int.class)
                )));
    }

    @ParameterizedTest
    @CsvSource({    // with method source, generate all decades
            "1950,1959",
            "1980,1989"
    })
    void demoStatsByYear(short year1, short year2){
        String queryJPQL = """
                SELECT
                    new org.example.dto.StatisticsByYear(
                        m.year,
                        COUNT(m),
                        COALESCE(SUM(m.duration), 0),
                        AVG(m.duration)
                    )
                FROM
                    Movie m
                WHERE
                    m.year BETWEEN :year1 and :year2
                GROUP BY
                    m.year
                ORDER BY
                    m.year
                """;
        entityManager.createQuery(queryJPQL, StatisticsByYear.class)
                .setParameter("year1", year1)
                .setParameter("year2", year2)
                .getResultStream()
                .forEach(System.out::println);
    }

    @Test
    void demoMovieWithActorsAndDirector() {
        int idMovie = 60196;
        var optMovie = Optional.ofNullable(entityManager.find(Movie.class, idMovie));
        Assertions.assertTrue(optMovie.isPresent());
        var movie = optMovie.get();
        System.out.println(movie);
        System.out.println(" - Director: " + movie.getDirector());
        System.out.println(" - Actors:");
        movie.getActors().forEach(System.out::println);
    }

    @ParameterizedTest
    @ValueSource(ints={142, 1466, 0})
    void demoFilmographyDirector(int director_id) {
        String queryJPQL = """
                SELECT
                    m
                FROM
                    Movie m
                    JOIN FETCH m.director d
                WHERE
                    d.id = :director_id
                ORDER BY
                    m.year DESC
                """;
        var movies = entityManager.createQuery(queryJPQL, Movie.class)
                .setParameter("director_id", director_id)
                .getResultList();
        System.out.println("- Filmography -");
        movies.forEach(movie -> {
            System.out.print(movie);
            System.out.println(" ; director = " + movie.getDirector());
        });
        movies.stream()
                .findFirst()
                .ifPresent(movie -> {
                    System.out.println(MessageFormat.format("- Casting movie: {0} -",
                            movie.getTitle()));
                    movie.getActors()
                            .forEach(System.out::println);
                });
    }

    @ParameterizedTest
    @ValueSource(ints={142, 537, 0})
    void demoFilmographyActor(int actor_id) {
        String queryJPQL = """
                SELECT
                    m
                FROM
                    Movie m
                    JOIN m.actors a
                    LEFT JOIN FETCH m.director d
                WHERE
                    a.id = :actor_id
                ORDER BY
                    m.year DESC
                """;
        var movies = entityManager.createQuery(queryJPQL, Movie.class)
                .setParameter("actor_id", actor_id)
                .getResultList();
        System.out.println("- Filmography -");
        movies.forEach(movie -> {
            System.out.print(movie);
            System.out.println(" ; director = " + movie.getDirector());
        });
    }

    // Exo: stats by director: id, name, countMovie, total duration, first year, last year ; keep only director with at least 10 movies

    // Exo: stats by actor: id, name, countMovie, total duration, first year, last year ; keep only actor with at least 10 movies

}
