package com.baseshelf.order;

import com.baseshelf.customer.Customer;
import com.baseshelf.state.StateCode;
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
    private Float totalDiscount;
    private Float totalAmountExcludingGst;
    private Float totalGst;
    private Float totalAmountIncludingGst;
    private String amountInWords;
    private Integer itemCount;
    private List<OrderItemResponse> orderItems;
    private Customer customer;
    private StoreResponse store;
}
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class OrderItemResponse{
    private Long id;
    private Integer quantity;
    private Float discountAmount;
    private Float amountExcludingGst;
    private Float cgst;
    private Float cgstAmount;
    private Float sgst;
    private Float sgstAmount;
    private Float gst;
    private Float gstAmount;
    private Float amountIncludingGst;
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
    private Float sellingPrice;
    private Float discountRate;
    private String unitOfMeasure;
    private String hsnCode;
}
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class StoreResponse{
    private String name;
    private String description;
    private String gstinNumber;
    private String contactNumber;
    private StateCode stateCode;
}
