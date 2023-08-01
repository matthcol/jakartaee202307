package org.example.data;

import lombok.*;
import org.example.validation.constraints.NotEmptyString;

import jakarta.validation.constraints.*;
import java.time.LocalDate;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class Person {
    // not null, no whitespaces
    @NotBlank
    private String name;

    // nullable, past date
    @PastOrPresent
    private LocalDate birthdate;

    // nullable, email format
    @NotEmptyString
    @Email
    private String email;

    // nullable, international format
    @Pattern(regexp="^\\+([0-9]( |-|\\.)?){6,14}[0-9]$")
    private String telephone;

}
