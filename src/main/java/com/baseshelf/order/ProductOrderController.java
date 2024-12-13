package com.baseshelf.order;

import lombok.RequiredArgsConstructor;
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

    @GetMapping("/filters")
    public List<ProductOrderResponseDto> getAllOrdersByStoreAndFilter(
            @PathVariable(name = "store-id") Long storeId,
            @ModelAttribute ProductOrderFilter filter
    ){
        System.out.println(filter.toString());
        return productOrderService.getAllByStoreAndFilter(storeId, filter);
    }

    @PostMapping("/lists")
    public ProductOrderResponse createOrderByList(
            @PathVariable(name = "store-id") Long storeId,
            @RequestBody List<Long> productIds
            ){
        return productOrderService.createOrderByIds(storeId, productIds);
    }

    @PostMapping("/body")
    public ProductOrderResponse createOrderByRequest(
            @PathVariable(name = "store-id") Long storeId,
            @RequestBody List<OrderRequest> orderRequests
            ){
        return productOrderService.createOrderByRequest(storeId, orderRequests);
    }
}
