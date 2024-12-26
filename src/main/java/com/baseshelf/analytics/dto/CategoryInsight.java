package com.baseshelf.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryInsight {
    private Long id;
    private String name;
    private Long productCount;
    private Long orderCount;
    private Long quantity;
    private Double revenue;
}
