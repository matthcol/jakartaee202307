package org.example.data;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class Movie {

    @NotBlank
    private String title;

    @Min(1888)
    private int year;

}
