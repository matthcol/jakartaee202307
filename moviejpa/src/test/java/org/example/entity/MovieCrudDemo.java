package org.example.entity;

import jakarta.persistence.EntityManager;
import org.example.bootstrap.JpaBootstrap;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MovieCrudDemo {

    static EntityManager entityManager;

    @BeforeAll
    static void bootstrapJpa() {
        entityManager = JpaBootstrap.createEntityManager();
    }

    @AfterAll
    static void shutdownJpa() {
        //        entityManager.close();
        JpaBootstrap.closeEntityManager(entityManager);
    }

    @Test
    void demoSave() {
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

}