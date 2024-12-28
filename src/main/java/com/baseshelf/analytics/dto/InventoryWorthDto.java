package com.baseshelf.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryWorthDto {
    private LocalDateTime date;
    private Double totalInvested;
    private Integer totalProducts;
    private Integer totalQuantity;
    private List<InventoryWorthInsight> analysis;
}
