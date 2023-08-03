package org.example.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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
                .getResultList(); // execute SQL
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

}
