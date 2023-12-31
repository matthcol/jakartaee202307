package org.example.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceException;
import org.example.bootstrap.JpaBootstrap;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(OrderAnnotation.class)
class MovieCrudDemo {

    static EntityManagerFactory entityManagerFactory;
    static EntityManager entityManager;

    @BeforeAll
    static void bootstrapJpa() {
        // Sol1. Bootstrap with Java code
        // entityManager = JpaBootstrap.createEntityManager();
        // Sol2. Bootstrap with PersistenceUnit defined in persistence.xml
        entityManagerFactory = Persistence.createEntityManagerFactory("DBH2");
        entityManager = entityManagerFactory.createEntityManager();
    }

    @AfterAll
    static void shutdownJpa() {
        // Sol1. Bootstrap with Java code
        // JpaBootstrap.closeEntityManager(entityManager);
        // Sol2. Bootstrap with PersistenceUnit defined in persistence.xml
        entityManager.close();
        entityManagerFactory.close();
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
    void testSaveMovieUniqueTitleYearKO(){
        System.out.println();
        System.out.println("*** Demo/Test Unique Title Year KO (with Hibernate) ***");
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

    @Test
    @Order(8)
    void demoUpdate() {
        System.out.println();
        System.out.println("*** Demo Update Movie (with Hibernate) ***");
        entityManager.getTransaction().begin();
        var optMovie = Optional.ofNullable(entityManager.find(Movie.class, 1));
        assertTrue(optMovie.isPresent());
        var movie = optMovie.get();
        movie.setDuration((short) 123);
        // entityManager.flush();
        entityManager.getTransaction().commit(); // SQL: update movies ... where id = 1
    }

    @Test
    @Order(9)
    void demoDelete() {
        System.out.println();
        System.out.println("*** Demo Delete Movies (with Hibernate) ***");
        // load all movies from database
        entityManager.getTransaction().begin();
        var movies = entityManager.createQuery("FROM Movie", Movie.class)
                .getResultList();
        System.out.println(" - movies from database -");
        movies.forEach(System.out::println);
        // delete movies from 2023
        short year = 2023;
        System.out.println(MessageFormat.format(" - delete movies of year {0} from database -", year));
        movies.stream()
                .filter(movie -> movie.getYear() == year)
                .peek(System.out::println)
                .forEach(entityManager::remove);
        entityManager.getTransaction().commit();
        // reread from database
        entityManager.clear();
        System.out.println(" - movies (read again) from database -");
        var movies2 = entityManager.createQuery("FROM Movie", Movie.class)
                .getResultList();
        movies2.forEach(System.out::println);
    }


}