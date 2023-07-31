package org.example.data;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;

// TODO: add constraint validation + test
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
    @Email
    private String email;

    // nullable, international format
    @Pattern(regexp="^(\\+[1-9][0-9]*(\\([0-9]*\\)|-[0-9]*-))?[0]?[1-9][0-9\\- ]*$")
    private String telephone;


}
