package org.example.entity;

import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.GenerationType.IDENTITY;

// lombok
@NoArgsConstructor // needed by JPA
@AllArgsConstructor // test
@Builder    // test
@Getter // needed by JPA
@Setter // needed by JPA
@ToString // test + log
// JPA
@Entity
public class Movie {
    // NB: id can be null if movie object is not saved in DB
    // PK will be generated by DB
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    private String title;

    private short year;

    @Transient // non persistent field, useful: bug, update
    private Short duration;
}