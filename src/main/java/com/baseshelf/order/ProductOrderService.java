package com.baseshelf.order;

import com.baseshelf.customer.Customer;
import com.baseshelf.product.Product;
import com.baseshelf.product.ProductService;
import com.baseshelf.store.Store;
import com.baseshelf.store.StoreService;
import com.baseshelf.utils.NumberToWordsConverter;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductOrderService {
    private final ProductOrderRepository productOrderRepository;
    private final ProductService productService;
    private final StoreService storeService;
    private final NumberToWordsConverter numberToWordsConverter;


//    @Bean
    @Order(value = 5)
    public CommandLineRunner insertOrder(
            ProductOrderRepository productOrderRepository,
            ProductService productService,
            StoreService storeService
    ){
        return args -> {
            List<Long> productIds = new ArrayList<>();
            List<Long> productIds2 = new ArrayList<>();
            List<Long> productIds3 = new ArrayList<>();
            List<Long> productIds4 = new ArrayList<>();
            Faker faker = new Faker();
            int max = faker.number().numberBetween(1, 10);
            for(int i = 0; i < max; i++){
                productIds.add((long) faker.number().numberBetween(251, 275));
                productIds2.add((long) faker.number().numberBetween(276, 325));
                productIds3.add((long) faker.number().numberBetween(326, 399));
                productIds4.add((long) faker.number().numberBetween(201, 399));
            }
        };
    }

    public ProductOrderResponseDto getById(Long storeId, Long orderId){
        Store store = storeService.getById(storeId);
        Optional<ProductOrder> productOrderOptional = productOrderRepository.findByStoreAndId(store,orderId);
        return this.orderResponseDtoMapper(productOrderOptional
                .orElseThrow(()-> new OrderNotFoundException("Order with id: " + orderId +  "do not exist!")));
    }

    public List<ProductOrderResponseDto> getAllByStoreAndFilter(Long storeId, ProductOrderFilter filter) {
        Specification<ProductOrder> specification = dynamicFilter(storeId, filter);
        List<ProductOrder> productOrders =  productOrderRepository.findAll(specification);
        return productOrders.stream()
                .map(this::orderResponseDtoMapper)
                .toList();
    }

//    public List<ProductOrder> findAllByStoreAndFilter(Long storeId, ProductOrderFilter filter) {
//        Specification<ProductOrder> specification = dynamicFilter(storeId, filter);
//        return productOrderRepository.findAll(specification);
//    }

    public void deleteByStoreAndId(Long storeId, Long orderId){
        Store store = storeService.getById(storeId);
        productOrderRepository.deleteByStoreAndId(store, orderId);
    }

    Specification<ProductOrder> dynamicFilter(Long storeId, ProductOrderFilter filter){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Store store = storeService.getById(storeId);
            predicates.add(criteriaBuilder.equal(root.get("store"), store));

            if(filter.from() != null){
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdOn"), filter.from()));
            }
            if(filter.to() != null){
                predicates.add(criteriaBuilder.lessThan(root.get("createdOn"), filter.to()));
            }
            if(filter.greaterThanAmount() != null){
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("totalAmount"), filter.greaterThanAmount()));
            }
            if(filter.lessThanAmount() != null){
                predicates.add(criteriaBuilder.lessThan(root.get("totalAmount"), filter.lessThanAmount()));
            }
            if(filter.greaterThanItem() != null){
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("itemCount"), filter.greaterThanItem()));
            }
            if(filter.lessThanItem() != null){
                predicates.add(criteriaBuilder.lessThan(root.get("itemCount"), filter.lessThanItem()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Transactional
    public ProductOrderResponseDto createOrder(Long storeId, ProductOrderRequest productOrderRequest) {
        Store store = storeService.getById(storeId);
        float totalAmountExcGst =  0F;
        float totalAmountIncGst =  0F;
        float totalGst =  0F;
        float discountedAmount = 0F;
        int itemCount = 0;
        OrderType orderType = OrderType.valueOf(productOrderRequest.getOrderType());

        Customer customer = productOrderRequest.getCustomer();
        Map<Long, Product> products;

        if(orderType.equals(OrderType.SALE))
             products = productService.validateProductsAndQuantity(store, productOrderRequest.getProductMap());
        else
            products = productService.returnProducts(store, productOrderRequest.getProductMap());

        ProductOrder productOrder = ProductOrder.builder()
                .name(LocalDateTime.now().toString())
                .totalDiscount(0f)
                .totalAmountExcludingGst(0f)
                .totalAmountIncludingGst(0f)
                .amountInWords("")
                .totalGst(0f)
                .itemCount(0)
                .orderTime(LocalTime.now())
                .customer(customer)
                .store(store)
                .build();

        List<OrderItem> orderItems = new ArrayList<>();
        for(ProductQuantityMap pqm: productOrderRequest.getProductMap()){
            Product product = products.get(pqm.getProductId());
            Integer quantity = pqm.getQuantity();

            Float cgst = product.getCgst();
            Float sgst = product.getSgst();
            Float gst = cgst+sgst;

            float discountAmount = product.getDiscountRate()/100 * product.getSellingPrice() * quantity * (orderType.equals(OrderType.SALE)? 1 : -1);
            Float amountExcGst = product.getSellingPrice() * quantity * (orderType.equals(OrderType.SALE)? 1 : -1) - discountAmount;
            Float gstAmount = amountExcGst * gst / 100;
            float amountIncGst = product.isTaxed() ? (amountExcGst + gstAmount) : amountExcGst ;


            totalAmountExcGst += amountExcGst;
            totalAmountIncGst += amountIncGst;
            discountedAmount += discountAmount;
            totalGst += product.isTaxed() ? gstAmount : 0;
            itemCount += quantity;

            OrderItem orderItem = OrderItem.builder()
                    .orderType(orderType)
                    .name(String.format("%s : %d", product.getName(), quantity))
                    .quantity(quantity)
                    .discountAmount(discountAmount)
                    .amountExcludingGst(amountExcGst)
                    .amountIncludingGst(amountIncGst)
                    .cgst(cgst)
                    .sgst(sgst)
                    .gst(gst)
                    .product(product)
                    .productOrder(productOrder)
                    .build();
            orderItems.add(orderItem);
        }

        String amountInWords = numberToWordCustom(totalAmountIncGst);
        productOrder.setAmountInWords(amountInWords);
        productOrder.setOrderItems(orderItems);
        productOrder.setItemCount(itemCount);
        productOrder.setTotalDiscount(discountedAmount);
        productOrder.setTotalAmountExcludingGst(totalAmountExcGst);
        productOrder.setTotalAmountIncludingGst(totalAmountIncGst);
        productOrder.setTotalGst(totalGst);

        productOrder = productOrderRepository.save(productOrder);
        return this.getById(storeId, productOrder.getId());
    }

    public String numberToWordCustom(Float number){
        BigDecimal value = BigDecimal.valueOf(number).setScale(2, RoundingMode.HALF_UP);
        return numberToWordsConverter.convert(value);
    }

    public ProductOrderResponseDto orderResponseDtoMapper(ProductOrder productOrder){
        return ProductOrderResponseDto.builder()
                .id(productOrder.getId())
                .createdOn(productOrder.getCreatedOn())
                .orderTime(productOrder.getOrderTime())
                .totalDiscount(productOrder.getTotalDiscount())
                .totalAmountExcludingGst(productOrder.getTotalAmountExcludingGst())
                .totalAmountIncludingGst(productOrder.getTotalAmountIncludingGst())
                .totalGst(productOrder.getTotalGst())
                .amountInWords(productOrder.getAmountInWords())
                .itemCount(productOrder.getItemCount())
                .orderItems(orderItemResponseMapper(productOrder.getOrderItems()))
                .customer(productOrder.getCustomer())
                .store(storeResponseMapper(productOrder.getStore()))
                .build();
    }

    private List<OrderItemResponse> orderItemResponseMapper(List<OrderItem> orderItems) {
        return orderItems.stream().map(
                (oi)->{
                    Float amountExcGst = oi.getAmountExcludingGst();
                    Product product = oi.getProduct();
                    return OrderItemResponse.builder()
                            .id(oi.getId())
                            .orderType(oi.getOrderType())
                            .discountAmount(oi.getDiscountAmount())
                            .amountExcludingGst(amountExcGst)
                            .cgst(oi.getCgst())
                            .cgstAmount(amountExcGst * oi.getCgst()/100)
                            .sgst(oi.getSgst())
                            .sgstAmount(amountExcGst * oi.getCgst()/100)
                            .gst(oi.getGst())
                            .gstAmount(amountExcGst * oi.getGst()/100)
                            .amountIncludingGst(oi.getAmountIncludingGst())
                            .quantity(oi.getQuantity())
                            .product(ProductResponse.builder()
                                    .id(product.getId())
                                    .name(product.getName())
                                    .hsnCode(product.getHsnCode())
                                    .unitOfMeasure(product.getUnitOfMeasure())
                                    .sellingPrice(product.getSellingPrice())
                                    .discountRate(product.getDiscountRate())
                                    .build())
                            .build();
                }
        ).toList();
    }

    private StoreResponse storeResponseMapper(Store store) {
        return StoreResponse.builder()
                .name(store.getName())
                .contactNumber(store.getContactNumber())
                .stateCode(store.getStateCode())
                .gstinNumber(store.getGstinNumber())
                .description(store.getDescription())
                .build();
    }
}
