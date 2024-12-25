package com.baseshelf.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrandInsightDateDto {
    private LocalDate date;
    private DayOfWeek weekDay;
    private List<BrandInsight> brandInsights;
}
