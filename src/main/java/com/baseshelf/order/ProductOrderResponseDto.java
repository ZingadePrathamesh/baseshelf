package com.baseshelf.order;

import com.baseshelf.customer.Customer;
import com.baseshelf.state.StateCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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
    private BigDecimal totalDiscount;
    private BigDecimal totalAmountExcludingGst;
    private BigDecimal totalGst;
    private BigDecimal totalAmountIncludingGst;
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
    private BigDecimal discountAmount;
    private BigDecimal amountExcludingGst;
    private Float cgst;
    private BigDecimal cgstAmount;
    private Float sgst;
    private BigDecimal sgstAmount;
    private Float gst;
    private BigDecimal gstAmount;
    private BigDecimal amountIncludingGst;
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
