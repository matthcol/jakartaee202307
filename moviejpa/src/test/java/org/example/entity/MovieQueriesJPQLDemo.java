package org.example.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.text.MessageFormat;

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



}
