package com.baseshelf.analytics.dto;

import com.baseshelf.brand.Brand;
import com.baseshelf.category.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDateDto {
    private LocalDate date;
    private Long productCounts;
    private Float sellingPrice;
    private Long soldQuantity;
    private Double revenue;
    private Brand brand;
    private List<Category> categoryList;
}
