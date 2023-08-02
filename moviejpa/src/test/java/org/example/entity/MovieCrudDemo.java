package org.example.entity;

import jakarta.persistence.EntityManager;
import org.example.bootstrap.JpaBootstrap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MovieCrudDemo {

    @Test
    void demoSave() {
        var movie = Movie.builder()
                .title("The Covenant")
                .year((short) 2023)
                .build();
    }

}