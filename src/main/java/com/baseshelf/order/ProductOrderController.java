package com.baseshelf.order;

import com.baseshelf.order.response.ProductOrderResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("stores/store-id/{store-id}/orders")
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

    @GetMapping("/order-id/{order-id}/receipt")
    public ResponseEntity<StreamingResponseBody> getReceipt(
            @PathVariable(name = "store-id") Long storeId,
            @PathVariable(name = "order-id") Long orderId
    ){
        return productOrderService.generateReceipt(storeId, orderId);
    }

    @PostMapping("/create")
    public ResponseEntity<ProductOrderResponseDto> createOrder(
            @PathVariable(name = "store-id") Long storeId,
            @RequestBody @Valid ProductOrderRequest productOrderRequest

    ){
        return productOrderService.createOrder(storeId, productOrderRequest);
    }

    @DeleteMapping("order-id/{order-id}")
    public void deleteByStoreAndId(
            @PathVariable(name = "store-id") Long storeId,
            @PathVariable(name = "order-id") Long orderId
    ){
        productOrderService.deleteByStoreAndId(storeId, orderId);
    }
}
