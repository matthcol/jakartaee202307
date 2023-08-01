package org.example.data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;


//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//@Getter
//@Setter
//@ToString
@Data
@Builder
public class Dummy {

    @Size(min=5)
    private String textMinSized;

    @Size(max=10)
    private String textMaxSized;

    @Size(min=5, max=10)
    private String textSized;

    @Size(min = 2, max = 5, message = "size list must be in range [{min}-{max}]")
    private List<String> textsSized;

    @DecimalMin("3.3") // hibernate provider accept float, double (not in api specification)
    // private double price;
    private BigDecimal price;

    // @NotNull
    @Valid // does not include @NotNull
    private Person person;

    @NotNull // applied to the list
    @Size(min=3) // applied to the list
    @Valid // applied to each person in the list
    private List<Person> persons1;

    @NotNull // applied to the list
    @Size(min=3) // applied to the list
    private List<@Valid Person> persons2;

    private List<@Size(min=3) @NotBlank String> listTextSized;

    private Set<@NotNull @Min(10) Integer> temperatures;

    private Map<@NotBlank @Size(min=3) String, @NotNull @Min(1) Integer> indexWords;
}
