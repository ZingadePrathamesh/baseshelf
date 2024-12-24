package com.baseshelf.analytics;

import com.baseshelf.brand.Brand;
import com.baseshelf.category.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryMonthDto {
    private String month;
    private Long productId;
    private Float sellingPrice;
    private Long soldQuantity;
    private Double revenue;
    private Brand brand;
    private List<Category> categoryList;
}
