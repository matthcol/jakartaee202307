package org.example.entity;


import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(OrderAnnotation.class)
public class MovieCrudRealDbDemo {
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
    @Order(1)
    void demoSave() {
        System.out.println();
        System.out.println("*** Demo Save (MariaDB) ***");
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


}
