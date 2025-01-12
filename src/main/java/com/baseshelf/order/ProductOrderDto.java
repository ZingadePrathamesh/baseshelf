package com.baseshelf.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductOrderDto{
    private Long id;

    private String name;

    private LocalDate createdOn;

    private LocalTime orderTime;

    private int itemCount;

    private BigDecimal totalAmount;

    private BigDecimal totalGst;
}
