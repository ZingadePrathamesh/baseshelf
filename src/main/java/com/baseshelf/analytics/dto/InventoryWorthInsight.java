package com.baseshelf.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryWorthInsight {
    private Long id;
    private Integer quantity;
    private Float costPrice;
    private Float sellingPrice;
    private Float productWorth;
}
