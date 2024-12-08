package com.baseshelf.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilter {
    private String name;
    private Long brandId;
    private List<Long> categoryIds;
    private LocalDate fromDate;
    private LocalDate toDate;
    private Float lowerPriceRange;
    private Float higherPriceRange;
    private Integer lowerQuantityRange;
    private Integer higherQuantityRange;
    private Float cgstRate;
    private Float sgstRate;
}
