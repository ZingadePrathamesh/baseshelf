package com.baseshelf.order.response;

import com.baseshelf.order.OrderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponse{
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
