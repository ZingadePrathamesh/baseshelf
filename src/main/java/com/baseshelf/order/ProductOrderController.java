package com.baseshelf.order;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("stores/{store-id}/orders")
public class ProductOrderController {
    private final ProductOrderService productOrderService;

    @GetMapping("/filters")
    public List<ProductOrderResponseDto> getAllOrdersByStoreAndFilter(
            @PathVariable(name = "store-id") Long storeId,
            @ModelAttribute ProductOrderFilter filter
    ){
        System.out.println(filter.toString());
        return productOrderService.getAllByStoreAndFilter(storeId, filter);
    }

    @PostMapping("/create")
    public ProductOrderResponseDto createOrder(
            @PathVariable(name = "store-id") Long storeId,
            @RequestBody @Valid ProductOrderRequest productOrderRequest

    ){
        return productOrderService.createOrder(storeId, productOrderRequest);
    }

    @DeleteMapping("/{order-id}")
    public void deleteByStoreAndId(
            @PathVariable(name = "store-id") Long storeId,
            @PathVariable(name = "order-id") Long orderId
    ){
        productOrderService.deleteByStoreAndId(storeId, orderId);
    }
}
