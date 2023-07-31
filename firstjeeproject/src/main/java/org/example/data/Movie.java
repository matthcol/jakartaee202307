package org.example.data;

import lombok.*;
import org.example.validation.constraints.Range;

import javax.validation.constraints.*;
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

    @Min(40)
    @Max(300)
    private Integer duration;

    @Range(min=40, max=300)
    private Integer duration2;

    @NotNull
    @Builder.Default
    private Set<String> genres = new TreeSet<>();

}
