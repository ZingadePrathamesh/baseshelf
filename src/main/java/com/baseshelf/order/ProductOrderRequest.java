package com.baseshelf.order;

import com.baseshelf.customer.Customer;
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
}
