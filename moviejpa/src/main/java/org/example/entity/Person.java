package org.example.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

// lombok
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
// JPA
@Entity
@Table(name = "persons")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 150)
    private String name;

    // if type is java.util.Date or Calendar => @Temporal
    private LocalDate birthdate;
}
