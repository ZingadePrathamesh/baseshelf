package com.baseshelf.analytics.dto;

import com.baseshelf.brand.Brand;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductMonthDto {
    private String month;
    private Long productId;
    private Float sellingPrice;
    private Long soldQuantity;
    private Double totalRevenue;
    private Brand brand;
}
