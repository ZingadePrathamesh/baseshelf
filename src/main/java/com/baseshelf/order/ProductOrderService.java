package com.baseshelf.order;

import com.baseshelf.customer.Customer;
import com.baseshelf.product.Product;
import com.baseshelf.product.ProductService;
import com.baseshelf.state.StateService;
import com.baseshelf.store.Store;
import com.baseshelf.store.StoreService;
import com.baseshelf.utils.InvalidGSTINException;
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
    private final StateService stateService;


//    @Bean
    @Order(value = 7)
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

    public ProductOrderResponseDto getDtoById(Long storeId, Long orderId){
        Store store = storeService.getById(storeId);
        Optional<ProductOrder> productOrderOptional = productOrderRepository.findByStoreAndId(store,orderId);
        return this.orderResponseDtoMapper(productOrderOptional
                .orElseThrow(()-> new OrderNotFoundException("Order with id: " + orderId +  "do not exist!")));
    }

    public ProductOrder getById(Long storeId, Long orderId){
        Store store = storeService.getById(storeId);
        Optional<ProductOrder> productOrderOptional = productOrderRepository.findByStoreAndId(store,orderId);
        return productOrderOptional
                .orElseThrow(()-> new OrderNotFoundException("Order with id: " + orderId +  "do not exist!"));
    }

    public List<ProductOrderResponseDto> getAllByStoreAndFilter(Long storeId, ProductOrderFilter filter) {
        Specification<ProductOrder> specification = dynamicFilter(storeId, filter);
        List<ProductOrder> productOrders =  productOrderRepository.findAll(specification);
        return productOrders.stream()
                .map(this::orderResponseDtoMapper)
                .toList();
    }

    public void validateCustomer(Customer customer){
        stateService.stateCodeExists(customer.getStateCode());
        String gstin = customer.getGstin();
        if(gstin.length() > 16 || gstin.length() < 15)
            throw new InvalidGSTINException("Customer's GSTIN is invalid");
    }


    //Incomplete implementation. Future scope
    @Transactional
    public ProductOrderResponseDto updateProductOrder(Long storeId, Long productOrderId, ProductOrderResponseDto orderResponseDto){
        ProductOrder productOrder = this.getById(storeId, productOrderId);
        return orderResponseDtoMapper(productOrder);
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
        validateCustomer(productOrderRequest.getCustomer());
        Store store = storeService.getById(storeId);
        Float totalAmountExcGst = 0F;
        Float totalAmountIncGst = 0F;
        Float totalGst =  0F;
        Float discountedAmount = 0F;
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
                .amountInWords("")
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

            Float discountAmount = product.getDiscountRate()/100 * product.getSellingPrice() * quantity * (orderType.equals(OrderType.SALE)? 1 : -1);
            Float amountExcGst = product.getSellingPrice() * quantity * (orderType.equals(OrderType.SALE)? 1 : -1) - discountAmount;
            Float gstAmount = amountExcGst * gst / 100;
            Float amountIncGst = product.isTaxed() ? (amountExcGst + gstAmount) : amountExcGst ;


            totalAmountExcGst += amountExcGst;
            totalAmountIncGst += amountIncGst;
            discountedAmount += discountAmount;
            totalGst += product.isTaxed() ? gstAmount : 0;
            itemCount += quantity;

            OrderItem orderItem = OrderItem.builder()
                    .orderType(orderType)
                    .name(String.format("%s : %d", product.getName(), quantity))
                    .quantity(quantity)
                    .discountAmount(BigDecimal.valueOf(discountAmount).setScale(2, RoundingMode.HALF_UP))
                    .amountExcludingGst(BigDecimal.valueOf(amountExcGst).setScale(2, RoundingMode.HALF_UP))
                    .amountIncludingGst(BigDecimal.valueOf(amountIncGst).setScale(2, RoundingMode.HALF_UP))
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
        productOrder.setTotalDiscount(BigDecimal.valueOf(discountedAmount).setScale(2, RoundingMode.HALF_UP));
        productOrder.setTotalAmountExcludingGst(BigDecimal.valueOf(totalAmountExcGst).setScale(2, RoundingMode.HALF_UP));
        productOrder.setTotalAmountIncludingGst(BigDecimal.valueOf(totalAmountIncGst).setScale(2, RoundingMode.HALF_UP));
        productOrder.setTotalGst(BigDecimal.valueOf(totalGst).setScale(2, RoundingMode.HALF_UP));

        productOrder = productOrderRepository.save(productOrder);
        return this.getDtoById(storeId, productOrder.getId());
    }

    public String numberToWordCustom(Float number){
        BigDecimal value = BigDecimal.valueOf(number).setScale(2, RoundingMode.HALF_UP);
        return numberToWordsConverter.convert(value) + " ONLY";
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
                    BigDecimal amountExcGst = oi.getAmountExcludingGst();
                    Product product = oi.getProduct();
                    return OrderItemResponse.builder()
                            .id(oi.getId())
                            .orderType(oi.getOrderType())
                            .discountAmount(oi.getDiscountAmount())
                            .amountExcludingGst(amountExcGst)
                            .cgst(oi.getCgst())
                            .cgstAmount((amountExcGst.multiply(BigDecimal.valueOf(oi.getCgst()/100))).setScale(2, RoundingMode.HALF_UP))
                            .sgst(oi.getSgst())
                            .sgstAmount((amountExcGst.multiply(BigDecimal.valueOf(oi.getSgst()/100))).setScale(2, RoundingMode.HALF_UP))
                            .gst(oi.getGst())
                            .gstAmount((amountExcGst.multiply(BigDecimal.valueOf(oi.getGst()/100))).setScale(2, RoundingMode.HALF_UP))
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
