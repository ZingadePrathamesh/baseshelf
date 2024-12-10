package com.baseshelf.order;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("stores/{store-id}/orders")
public class ProductOrderController {
    private final ProductOrderService productOrderService;

    @PostMapping("")
    public OrderResponse createOrder(
            @PathVariable(name = "store-id") Long storeId,
            @RequestBody List<Long> productIds
            ){
        return productOrderService.createNewOrder(storeId, productIds);
    }
}
