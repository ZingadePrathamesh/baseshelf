package com.baseshelf.order;

import com.baseshelf.product.Product;
import com.baseshelf.product.ProductService;
import com.baseshelf.store.Store;
import com.baseshelf.store.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductOrderService {
    private final ProductOrderRepository productOrderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductService productService;
    private final StoreService storeService;

    @Bean
    @Order(value = 5)
    public CommandLineRunner insertOrder(
        ProductOrderRepository productOrderRepository,
        OrderItemRepository orderItemRepository,
        ProductService productService,
        StoreService storeService
    ){
        return args -> {
        };
    }

    public ProductOrderResponse createOrderByIds(Long storeId, List<Long> productIds){
        Store store = storeService.getById(storeId);
        float totalAmount= 0.0f;
        float totalGst = 0.0f;

        Map<Long, Integer> productMap = new HashMap<>();
        productIds.forEach((id)-> productMap.merge(id, 1, Integer::sum));

        Set<Product> products = productService.validateProductsAndQuantity(storeId, productMap);

        ProductOrder productOrder = ProductOrder.builder()
                .name(LocalDateTime.now().toString())
                .store(store)
                .orderTime(LocalTime.now())
                .totalAmount(totalAmount)
                .totalGst(totalGst)
                .build();

        Set<OrderItem> orderItems = new HashSet<>();
        for(Product product: products){
            int quantity = productMap.get(product.getId());
            float gst = product.isTaxed() ? (product.getSellingPrice() * quantity * (product.getCgst()+product.getSgst()) / 100) : 0;
            float amount = product.getSellingPrice() * quantity + gst;

            totalAmount += amount;
            totalGst += gst;

            orderItems.add(OrderItem.builder()
                    .name(product.getName() + " " + quantity)
                    .quantity(quantity)
                    .product(product)
                    .amount(amount)
                    .gst(gst)
                    .productOrder(productOrder)
                    .build());
        }

        productOrder.setTotalAmount(totalAmount);
        productOrder.setTotalGst(totalGst);
        productOrder.setOrderItems(new ArrayList<>(orderItems));

        productOrder = productOrderRepository.save(productOrder);

        return this.getById(storeId, productOrder.getId());
    }

    public ProductOrderResponse getById(Long storeId, Long orderId){
        Store store = storeService.getById(storeId);
        return productOrderRepository.findResponseByStoreAndId(store, orderId)
                .orElseThrow(()-> new OrderNotFoundException("Order with id: " + orderId +  "do not exist!"));
    }

    public List<ProductOrderResponse> getAllByStore(Long storeId) {
        Store store = storeService.getById(storeId);
        return productOrderRepository.findAllByStore(store);
    }
}
