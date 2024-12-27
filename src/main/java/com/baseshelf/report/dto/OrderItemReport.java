package com.baseshelf.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemReport {
    private LocalDate date;
    private Long id;
    private Long product;
    private Integer quantity;
    private Long productOrder;
    private Float amount;
}
