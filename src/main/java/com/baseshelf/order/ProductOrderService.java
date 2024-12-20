package com.baseshelf.order;

import com.baseshelf.analytics.RevenueMonthDto;
import com.baseshelf.analytics.RevenuePerDate;
import com.baseshelf.product.Product;
import com.baseshelf.product.ProductService;
import com.baseshelf.store.Store;
import com.baseshelf.store.StoreService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductOrderService {
    private final ProductOrderRepository productOrderRepository;
    private final ProductService productService;
    private final StoreService storeService;

    public ProductOrderResponseDto createOrderByIds(Long storeId, List<Long> productIds){
        Map<Long, Integer> productMap = new HashMap<>();
        productIds.forEach((id)-> productMap.merge(id, 1, Integer::sum));
        return createOrderByProductMap(storeId, productMap);
    }

    public ProductOrderResponseDto createOrderByRequest(Long storeId, List<OrderRequest> orderRequests){
        Map<Long, Integer> productMap = new HashMap<>();
        for(OrderRequest or: orderRequests){
            productMap.merge(or.getProductId(), or.getQuantity(), Integer::sum);
        }
        return createOrderByProductMap(storeId, productMap);
    }

    public ProductOrderResponseDto createOrderByProductMap(Long storeId, Map<Long, Integer> productMap){
        Store store = storeService.getById(storeId);
        float totalAmount= 0.0f;
        float totalGst = 0.0f;
        int itemCount = 0;

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
            itemCount += quantity;

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
        productOrder.setItemCount(itemCount);
        productOrder.setOrderItems(new ArrayList<>(orderItems));

        productOrder = productOrderRepository.save(productOrder);

        return this.getById(storeId, productOrder.getId());
    }

    public ProductOrderResponseDto getById(Long storeId, Long orderId){
        Store store = storeService.getById(storeId);
        Optional<ProductOrder> productOrderOptional = productOrderRepository.findByStoreAndId(store,orderId);
        return this.productOrderResponseMapper(productOrderOptional
                .orElseThrow(()-> new OrderNotFoundException("Order with id: " + orderId +  "do not exist!")));
    }

    public List<ProductOrderResponse> getAllByStore(Long storeId) {
        Store store = storeService.getById(storeId);
        return productOrderRepository.findAllByStore(store);
    }

    public List<ProductOrderResponseDto> getAllByStoreAndFilter(Long storeId, ProductOrderFilter filter) {
        Specification<ProductOrder> specification = dynamicFilter(storeId, filter);
        List<ProductOrder> productOrders =  productOrderRepository.findAll(specification);
        return productOrders.stream()
                .map(this::productOrderResponseMapper)
                .toList();
    }

    public void deleteByStoreAndId(Long storeId, Long orderId){
        Store store = storeService.getById(storeId);
        productOrderRepository.deleteByStoreAndId(store, orderId);
    }

    private ProductOrderResponseDto productOrderResponseMapper(ProductOrder productOrder) {
        return ProductOrderResponseDto.builder()
                .id(productOrder.getId())
                .orderTime(productOrder.getOrderTime())
                .createdOn(productOrder.getCreatedOn())
                .totalGst(productOrder.getTotalGst())
                .totalAmount(productOrder.getTotalAmount())
                .itemCount(productOrder.getItemCount())
                .orderItems(
                        productOrder.getOrderItems().stream().map(this::orderItemResponseMapper).toList()
                )
                .store(StoreResponse.builder()
                        .gstinNumber(productOrder.getStore().getGstinNumber())
                        .description(productOrder.getStore().getDescription())
                        .name(productOrder.getStore().getName())
                        .build()
                )
                .build();
    }

    public OrderItemResponse orderItemResponseMapper(OrderItem orderItem){
        return OrderItemResponse.builder()
                .id(orderItem.getId())
                .gst(orderItem.getGst())
                .amount(orderItem.getAmount())
                .quantity(orderItem.getQuantity())
                .product(ProductResponse.builder()
                        .id(orderItem.getProduct().getId())
                        .cgst(orderItem.getProduct().getCgst())
                        .sgst(orderItem.getProduct().getSgst())
                        .sellingPrice(orderItem.getProduct().getSellingPrice())
                        .build())
                .build();
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

    public List<RevenuePerDate> getRevenueByDate(Long storeId, LocalDate date) {
        Store store = storeService.getById(storeId);
        List<Object[]> results = productOrderRepository.findRevenueByDateRange(store, date, date);
        return results.stream()
                .map(result -> new RevenuePerDate((LocalDate) result[0], (Double) result[1]))
                .collect(Collectors.toList());
    }

    public List<RevenuePerDate> getRevenueByDateRange(Long storeId, LocalDate from, LocalDate to) {
        Store store = storeService.getById(storeId);
        List<Object[]> results =  productOrderRepository.findRevenueByDateRange(store, from, to);
        List<RevenuePerDate> revenuePerDates = new ArrayList<>();
        return results.stream()
                .map(result -> new RevenuePerDate((LocalDate) result[0], (Double) result[1]))
                .collect(Collectors.toList());
    }

    public List<RevenueMonthDto> getRevenueForMonthsAndYear(Long storeId, Integer year, List<Integer> months) {
        Store store = storeService.getById(storeId);
        List<Object[]> results = productOrderRepository.findRevenueByMonthAndYear(store, year, months);
        List<RevenueMonthDto> revenueMonthDtos = results.stream()
                .map(result-> new RevenueMonthDto(getMonth((Integer) result[0]), (Integer) result[0], (Double) result[1], null))
                .toList();

        for(int i = 0; i<revenueMonthDtos.size(); i++){
            if (i == 0) revenueMonthDtos.get(i).setGrowthPercentage(0D);
            else{
                double previousMonthRevenue = revenueMonthDtos.get(i-1).getRevenue();
                double currentMonthRevenue = revenueMonthDtos.get(i).getRevenue();
                double profitPercent = (currentMonthRevenue/previousMonthRevenue - 1) * 100;
                revenueMonthDtos.get(i).setGrowthPercentage(profitPercent);
            }
        }
        return revenueMonthDtos;
    }

    public static String getMonth(int month){
        return switch (month){
            case 1:
                yield "January";
            case 2:
                yield "February";
            case 3:
                yield "March";
            case 4:
                yield "April";
            case 5:
                yield "May";
            case 6:
                yield "June";
            case 7:
                yield "July";
            case 8:
                yield "August";
            case 9:
                yield "September";
            case 10:
                yield "October";
            case 11:
                yield "November";
            case 12:
                yield "December";
            default :
                throw new IllegalStateException("Unexpected value: " + month);
        };
    }
}
