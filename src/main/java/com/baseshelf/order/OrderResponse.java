package com.baseshelf.order;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long orderId;
    private LocalDate orderDate;
    private LocalTime orderTime;
    private Float totalAmount;
    private Float totalGst;
    private Integer totalProducts;
    private List<OrderItemResponse> orderItemResponseList;
    private StoreResponse storeResponse;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class OrderItemResponse{
    private Long productId;
    private int quantity;
    private float price;
    private float gst;
    private float gstPercent;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class StoreResponse{
    private String name;
    private String description;
    private String address;
    private String gstin;
}
