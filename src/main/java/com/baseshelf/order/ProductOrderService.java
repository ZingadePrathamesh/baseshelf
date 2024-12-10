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

import java.time.LocalDate;
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
            createOrderByListOfProducts(2L, List.of(202L, 203L, 203L, 204L));
        };
    }

    public ProductOrder createOrderByListOfProducts(Long storeId, List<Long> productIds){
        Store store = storeService.getById(storeId);
        ProductOrder order = ProductOrder.builder()
                .orderItems(null)
                .store(store)
                .productTotalAmount(null)
                .name(LocalDate.now().toString())
                .build();

        ProductOrder savedOrder = productOrderRepository.save(order);

        Map<Long, Integer> productQuantity = new HashMap<>();
        for(Long id: productIds){
            Integer value = productQuantity.get(id);
            value = value == null ? 0 : value;
            productQuantity.put(id, value + 1);
        }

        Set<OrderItem> orderItems = new HashSet<>();
        productQuantity.forEach((key,value) ->{
            Product product = productService.getByIdAndStore(key, storeId);
            float amount = (product.getSellingPrice() + (product.isTaxed()?(product.getSellingPrice()*2*product.getCgst()/100):0));
            orderItems.add(
                    OrderItem.builder()
                            .productOrder(order)
                            .quantity(value)
                            .product(productService.getByIdAndStore(key, storeId))
                            .name("order product " + key)
                            .amount(amount)
                            .build()
            );
        });
        savedOrder.setOrderItems(new ArrayList<>(orderItemRepository.saveAll(orderItems)));
        return new ProductOrder();
    }
}
