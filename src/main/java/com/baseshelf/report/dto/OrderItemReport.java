package com.baseshelf.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemReport {
    private Long orderId;
    private Long itemId;
    private LocalDate date;
    private DayOfWeek weekDay;
    private Long productId;
    private String productName;
    private Float sellingPrice;
    private Integer quantity;
    private Float amount;
    private Long brandId;
    private String brandName;

    public OrderItemReport(Long orderId, Long itemId, LocalDate date, Long productId, String productName, Float sellingPrice, Integer quantity, Float amount, Long brandId, String brandName) {
        this.orderId = orderId;
        this.itemId = itemId;
        this.date = date;
        this.weekDay = DayOfWeek.from(date);
        this.productId = productId;
        this.productName = productName;
        this.sellingPrice = sellingPrice;
        this.quantity = quantity;
        this.amount = amount;
        this.brandId = brandId;
        this.brandName = brandName;
    }
}
