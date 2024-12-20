package com.baseshelf.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsByBrandDate {
    private Long brandId;
    private LocalDate date;
    private Long quantity;
    private Long products;
    private Double revenue;
    private String weekDay;
}
