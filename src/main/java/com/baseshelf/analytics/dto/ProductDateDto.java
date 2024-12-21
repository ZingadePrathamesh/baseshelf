package com.baseshelf.analytics.dto;

import com.baseshelf.brand.Brand;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDateDto {
    private LocalDate date;
    private Long productId;
    private Float sellingPrize;
    private Long soldQuantity;
    private Double totalRevenue;
    private Brand brand;
}
