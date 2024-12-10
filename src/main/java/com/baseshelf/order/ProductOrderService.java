package com.baseshelf.order;

import com.baseshelf.product.Product;
import com.baseshelf.product.ProductService;
import com.baseshelf.store.Store;
import com.baseshelf.store.StoreService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    @Transactional
    public OrderResponse createNewOrder(Long storeId, List<Long> productIds){
        float totalAmount = 0.0f;
        float totalGst = 0.0f;
        Store store = storeService.getById(storeId);

        Map<Long, Integer> productQuantity = new HashMap<>(productIds.size());
        productIds.forEach((id) -> productQuantity.merge(id, 1, Integer::sum));

        List<OrderItem> orderItems = new ArrayList<OrderItem>();
        productQuantity.forEach((productId, quantity) -> {
            Product product = productService.getByIdAndStore(productId, store);
            float gst = product.isTaxed() ? (product.getSellingPrice() * 2 * product.getCgst() / 100) * quantity : 0;
            float amount = product.getSellingPrice() * quantity + gst;

            OrderItem orderItem = OrderItem.builder()
                    .name(product.getName() + " " +quantity)
                    .quantity(quantity)
                    .product(product)
                    .amount(amount)
                    .gst(gst)
                    .build();
            orderItems.add(orderItem);
        });

        for (OrderItem item : orderItems) {
            totalAmount += item.getAmount();
            totalGst += item.getGst();
        }

        ProductOrder productOrder = ProductOrder.builder()
                .name(LocalDateTime.now().toString())
                .orderItems(orderItems)
                .store(store)
                .orderTime(LocalTime.now())
                .productTotalAmount(totalAmount)
                .totalGst(totalGst)
                .build();

        ProductOrder order = productOrderRepository.save(productOrder);
        return OrderResponse.builder()
                .orderId(order.getId())
                .orderDate(LocalDate.now())
                .orderTime(LocalTime.now())
                .totalProducts(order.getOrderItems().size())
                .totalGst(totalGst)
                .totalAmount(totalAmount)
                .storeResponse(
                        StoreResponse.builder()
                                .address(store.getAddress())
                                .gstin(store.getGstinNumber())
                                .name(store.getName())
                                .description(store.getDescription())
                                .build()
                )
                .orderItemResponseList(
                        orderItems.stream()
                                .map((item)->
                                    OrderItemResponse.builder()
                                            .productId(item.getProduct().getId())
                                            .price(item.getAmount())
                                            .gst(item.getGst())
                                            .gstPercent((item.getProduct().getCgst())*2)
                                            .quantity(item.getQuantity())
                                            .build()
                                )
                                .toList()
                )
                .build();
    }

}
