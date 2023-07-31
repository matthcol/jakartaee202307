package org.example.data;

import java.time.LocalDate;

// TODO: add constraint validation + test
public class Person {
    // not null, no whitespaces
    private String name;

    // nullable, past date
    private LocalDate birthdate;

    // nullable, email format
    private String email;

    // nullable, international format
    private String telephone;
}
