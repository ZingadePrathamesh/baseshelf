package com.baseshelf.product;

import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
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
    @PositiveOrZero(message = "Cannot be a negative value")
    private Long brandId;
    private List<Long> categoryIds;
    @PastOrPresent(message = "cannot be a future date")
    private LocalDate from;
    @PastOrPresent(message = "cannot be a future date")
    private LocalDate to;
    @PositiveOrZero(message = "Cannot be a negative value")
    private Float lowerPrice;
    @PositiveOrZero(message = "Cannot be a negative value")
    private Float higherPrice;
    @PositiveOrZero(message = "Cannot be a negative value")
    private Integer lowerQuantity;
    @PositiveOrZero(message = "Cannot be a negative value")
    private Integer higherQuantity;
    @PositiveOrZero(message = "Cannot be a negative value")
    private Float cgst;
    @PositiveOrZero(message = "Cannot be a negative value")
    private Float sgst;
}
