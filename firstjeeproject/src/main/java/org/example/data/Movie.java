package org.example.data;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.Set;
import java.util.TreeSet;

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

    // TODO: nullable, min 40, max 300
    private Integer duration;

    // TODO: not null, can be empty
    @Builder.Default
    private Set<String> genres = new TreeSet<>();

}
