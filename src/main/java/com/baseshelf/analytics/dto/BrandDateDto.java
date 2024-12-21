package com.baseshelf.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrandDateDto {
    private Long brandId;
    private LocalDate date;
    private Long quantity;
    private Long products;
    private Double revenue;
    private String weekDay;
}
