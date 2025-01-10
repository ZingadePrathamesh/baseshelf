package com.baseshelf.order;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private Float totalAmount;

    private Float totalGst;
}
