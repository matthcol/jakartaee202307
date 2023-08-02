package org.example.entity;

import jakarta.persistence.EntityManager;
import org.example.bootstrap.JpaBootstrap;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(OrderAnnotation.class)
class MovieCrudDemo {

    static EntityManager entityManager;

    @BeforeAll
    static void bootstrapJpa() {
        entityManager = JpaBootstrap.createEntityManager();
    }

    @AfterAll
    static void shutdownJpa() {
        JpaBootstrap.closeEntityManager(entityManager);
    }

    @Test
    @Order(1)
    void demoSave() {
        System.out.println();
        System.out.println("*** Demo Save with Hibernate ***");
        var movie = Movie.builder()
                .title("The Covenant")
                .year((short) 2023)
                .build();
        entityManager.getTransaction().begin();
        entityManager.persist(movie); // INSERT here or later
        entityManager.flush();
        System.out.println(movie);
        entityManager.getTransaction().commit();
    }

    @Test
    @Order(2)
    void demoReadById() {
        System.out.println();
        System.out.println("*** Demo Read by id with Hibernate ***");
        // movie is null if not found
        Movie movie = entityManager.find(Movie.class, 1);
        // NB: Spring => Optional<Movie>
        Optional<Movie> optMovie = Optional.ofNullable(movie);
        System.out.println(optMovie);
    }

}