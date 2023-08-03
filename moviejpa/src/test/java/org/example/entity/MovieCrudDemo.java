package org.example.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import org.example.bootstrap.JpaBootstrap;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import java.util.List;
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
        System.out.println(movie);  // id has been provided
        assertNotNull(movie.getId());
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

    @Test
    @Order(3)
    void demoReadByIdEmptyCache() {
        System.out.println();
        System.out.println("*** Demo Read by id with empty cache with Hibernate ***");
        // empty hibernate/JPA cache
        entityManager.clear();
        // movie is null if not found
        Movie movie = entityManager.find(Movie.class, 1);
        // NB: Spring => Optional<Movie>
        Optional<Movie> optMovie = Optional.ofNullable(movie);
        System.out.println(optMovie);
    }

    @Test
    @Order(4)
    void demoSaveSeveral(){
        System.out.println();
        System.out.println("*** Demo Save several movies with Hibernate ***");
        var movies = List.of(
                Movie.builder()
                        .title("The Batman")
                        .year((short) 2022)
                        .build(),
                Movie.builder()
                        .title("Barbie")
                        .year((short) 2023)
                        .duration((short) 114)
                        .build(),
                Movie.builder()
                        .title("Talk to Me")
                        .year((short) 2022)
                        .duration((short) 95)
                        .build()
        );
        entityManager.getTransaction().begin();
        movies.forEach(entityManager::persist);
        entityManager.flush();
        entityManager.getTransaction().commit();
        // check all movies have an id
        movies.forEach(System.out::println);
        assertAll(movies.stream()
                .map(movie -> () -> assertNotNull(movie.getId()))
        );
    }

    @Test
    @Order(5)
    void testSaveMovieTitleMandatoryKO() {
        System.out.println();
        System.out.println("*** Demo Error Save movie without title (with Hibernate) ***");
        var movie = Movie.builder()
                .year((short) 2023)
                .build();
        assertThrows(PersistenceException.class, () -> {
            entityManager.getTransaction().begin();
            entityManager.persist(movie);
            entityManager.flush();
            entityManager.getTransaction().commit();
        });
        entityManager.getTransaction().rollback();
    }

    @Test
    @Order(6)
    void demoTransactionRollback(){
        System.out.println();
        System.out.println("*** Demo Rollback Transaction (with Hibernate) ***");
        var movies = List.of(
                Movie.builder()
                        .title("Star Wars IV - A New Hope")
                        .year((short) 1977)
                        .build(),
                Movie.builder()
                        .title("Star Wars V - The Empire Strikes Back")
                        .year((short) 1977)
                        .build(),
                Movie.builder()
                        .title("Star Wars VI - Return of The Jedi")
                        .year((short) 1977)
                        .build()
        );
        entityManager.getTransaction().begin();
        for (var movie: movies) {
            entityManager.persist(movie);
            entityManager.flush();
            System.out.println(movie);
        }
        System.out.println("Rollback");
        entityManager.getTransaction().rollback(); // .commit()
        entityManager.clear();
        var moviesFromDb = entityManager.createQuery("FROM Movie", Movie.class)
                .getResultList();
        System.out.println(moviesFromDb);
    }

    @RepeatedTest(3)
    @Order(7)
    void demoSaveMovieUniqueTitleYearKO(){
        var movie1 =  Movie.builder()
                .title("Star Wars IV - A New Hope")
                .year((short) 1977)
                .build();
        var movie2 = Movie.builder()
                .title("Star Wars IV - A New Hope")
                .year((short) 1977)
                .duration((short) 121)
                .build();
        entityManager.getTransaction().begin();
        // insert movie (first version)
        assertDoesNotThrow(() -> {
            entityManager.persist(movie1);
            entityManager.flush();
            System.out.println("Movie saved: " + movie1);
        });
        // insert same movie again
        assertThrows(PersistenceException.class, () -> {
            entityManager.persist(movie2);
            entityManager.flush();
            entityManager.getTransaction().commit();
            System.out.println("Movie saved: " + movie2);
        });
        entityManager.getTransaction().rollback();
    }

}