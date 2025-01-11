package com.baseshelf.order;

import com.baseshelf.customer.Customer;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductOrderRequest {
    private List<ProductQuantityMap> productMap;
    private Customer customer;
    @NotNull(message = "Order type must be either SALE|RETURN and cannot be null")
    @Pattern(regexp = "RETURN|SALE", message = "Order type must be SALE or RETURN.")
    private String orderType;
}
