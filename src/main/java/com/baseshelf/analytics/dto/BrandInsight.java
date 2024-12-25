package com.baseshelf.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandInsight{
    private Long brandId;
    private String brandName;
    //distinct products
    private Long productCount;
    //orders that contain the product
    private Long orderCount;
    //the total quantity of the product
    private Long quantity;
    private Double revenue;
}
