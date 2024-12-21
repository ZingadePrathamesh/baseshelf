package com.baseshelf.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BrandMonthDto {
    private Long brandId;
    private String month;
    private Long products;
    private Long orderCount;
    private Long soldQuantity;
    private Double revenue;
    private Double profitPercent;
}
