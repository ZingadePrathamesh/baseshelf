package com.baseshelf.order;

import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("stores/{store-id}/orders")
public class ProductOrderController {
    private final ProductOrderService productOrderService;

    @GetMapping("")
    public List<ProductOrderResponse> getAllOrdersByStore(
            @PathVariable(name = "store-id") Long storeId
    ){
        return productOrderService.getAllByStore(storeId);
    }

    @PostMapping("")
    public ProductOrderResponse createOrder(
            @PathVariable(name = "store-id") Long storeId,
            @RequestBody List<Long> productIds
            ){
        return productOrderService.createOrderByIds(storeId, productIds);
    }
}
