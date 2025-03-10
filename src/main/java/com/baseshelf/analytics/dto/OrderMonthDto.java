package com.baseshelf.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderMonthDto {
    private String month; // jan to dec
    private Integer monthNumber; // 1 to 12
    private Long itemCount;
    private Double revenue; // revenue of that month
    private Double growthPercentage; //profit made compare to previous month
}
