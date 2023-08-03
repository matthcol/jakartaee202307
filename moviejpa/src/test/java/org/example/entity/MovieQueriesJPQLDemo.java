package org.example.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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

    // Exo: movies with no duration sorted by title

    // Exo: movies from last 10 years sorted by year desc, title asc




}
