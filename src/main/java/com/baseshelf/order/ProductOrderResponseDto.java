package com.baseshelf.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductOrderResponseDto {
    private Long id;
    private LocalDate createdOn;
    private LocalTime orderTime;
    private Float totalAmount;
    private Float totalGst;
    private Integer itemCount;
    private List<OrderItemResponse> orderItems;
    private StoreResponse store;
}
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class OrderItemResponse{
    private Long id;
    private Integer quantity;
    private Float amount;
    private Float gst;
    private ProductResponse product;
    private OrderType orderType;
}
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class ProductResponse{
    private Long id;
    private String name;
    private Float cgst;
    private Float sgst;
    private Float sellingPrice;
}
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class StoreResponse{
    private String name;
    private String description;
    private String gstinNumber;
}
