package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
//@Getter
//@ToString
@Data
public class TitleYear {
    private String title;
    private short year;
}
