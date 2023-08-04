package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatisticsByYear {
    private short year;
    private long movieCount;
    private long totalDuration;
    private Double averageDuration;
}
