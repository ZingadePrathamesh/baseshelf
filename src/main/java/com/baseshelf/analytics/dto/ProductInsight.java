package com.baseshelf.analytics.dto;

import com.baseshelf.brand.Brand;
import com.baseshelf.category.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductInsight {
    private Long    id;
    private String  name;
    private Float   sellingPrice;
    private Long    quantity;
    private Long    orderCount;
    private Float   revenue;
    private Long    brandId;
    private String  brandName;
}
