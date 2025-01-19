package com.baseshelf.order.response;

import com.baseshelf.customer.Customer;
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
    private String receiptBase64; // Encoded receipt image
}
