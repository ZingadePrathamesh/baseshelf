package com.baseshelf.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnalyticsByBrand {
    private String month;
    private Long orderCount;
    private Long soldQuantity;
    private Double revenue;
    private Double profitPercent;
}
