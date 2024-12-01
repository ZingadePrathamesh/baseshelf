package com.baseshelf.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilter {
    private Float lessThan;
    private Float greaterThan;
    private LocalDate createdOn;
//    private List<Category> categories;
}
