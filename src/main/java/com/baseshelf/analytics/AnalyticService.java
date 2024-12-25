package com.baseshelf.analytics;

import com.baseshelf.analytics.dto.*;
import com.baseshelf.brand.Brand;
import com.baseshelf.brand.BrandService;
import com.baseshelf.category.Category;
import com.baseshelf.category.CategoryService;
import com.baseshelf.order.OrderItemRepository;
import com.baseshelf.order.ProductOrderRepository;
import com.baseshelf.product.ProductRepository;
import com.baseshelf.product.ProductService;
import com.baseshelf.store.Store;
import com.baseshelf.store.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class AnalyticService {
    private final StoreService storeService;
    private final ProductOrderRepository productOrderRepository;
    private final BrandService brandService;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final CategoryService categoryService;

    public List<OrderDateDto> getRevenueByDate(Long storeId, LocalDate date) {
        Store store = storeService.getById(storeId);
        List<Object[]> results = productOrderRepository.findOrdersAnalysisByDateRange(store, date, date);
        return results.stream()
                .map(result -> new OrderDateDto((LocalDate) result[0], (Double) result[1], (Long) result[2]))
                .collect(Collectors.toList());
    }

    public List<OrderDateDto> getRevenueByDateRange(Long storeId, LocalDate from, LocalDate to) {
        Store store = storeService.getById(storeId);
        List<Object[]> results =  productOrderRepository.findOrdersAnalysisByDateRange(store, from, to);
        List<OrderDateDto> orderDateDtos = new ArrayList<>();
        return results.stream()
                .map(result -> new OrderDateDto((LocalDate) result[0], (Double) result[1], (Long) result[2]))
                .collect(Collectors.toList());
    }

    public List<OrderMonthDto> getRevenueForMonthsAndYear(Long storeId, Integer year, List<Integer> months) {
        if (storeId == null || year == null || months == null || months.isEmpty()) {
            throw new IllegalArgumentException("Invalid input: storeId, year, and months must be non-null, and months must not be empty.");
        }

        Store store = storeService.getById(storeId);
        List<Object[]> results = productOrderRepository.findRevenueByMonthAndYear(store, year, months);
        List<OrderMonthDto> orderMonthDtos = results.stream()
                .map(result-> new OrderMonthDto(getMonth((Integer) result[0]), (Integer) result[0], (Long) result[1] , (Double) result[2], null))
                .toList();

        IntStream.range(0, orderMonthDtos.size())
                .forEach( i ->{
                    if (i == 0) orderMonthDtos.get(i).setGrowthPercentage(0D);
                    else{
                        double previousMonthRevenue = orderMonthDtos.get(i-1).getRevenue();
                        double currentMonthRevenue = orderMonthDtos.get(i).getRevenue();
                        double profitPercent = previousMonthRevenue == 0 ? 0 : (currentMonthRevenue/previousMonthRevenue - 1) * 100;
                        orderMonthDtos.get(i).setGrowthPercentage(profitPercent);
                    }
                });
        return orderMonthDtos;
    }

    public List<BrandInsightMonthDto> brandInsightMonthDtoMapper(List<Object[]> results, Integer lowerMonth, Integer upperMonth) {
        // Use LinkedHashMap to maintain month order
        Map<Integer, BrandInsightMonthDto> insightMonthDtoMap = new LinkedHashMap<>();
        // Ensure all months in the range are included, even if empty
        for (int i = lowerMonth; i <= upperMonth; i++) {
            insightMonthDtoMap.computeIfAbsent(
                    i,
                    m -> new BrandInsightMonthDto(m, getMonth(m), new ArrayList<>())
            );
        }
        // Populate map only for months with data
        for (Object[] result : results) {
            Integer month = (Integer) result[0];
            // Get or create the month DTO
            BrandInsightMonthDto monthDto = insightMonthDtoMap.computeIfAbsent(
                    month,
                    m -> new BrandInsightMonthDto(m, getMonth(m), new ArrayList<>())
            );
            // Add the brand insight to the month's analysis
            monthDto.getAnalysis().add(
                    BrandInsight.builder()
                            .brandId((Long) result[1])
                            .brandName((String) result[2])
                            .productCount((Long) result[3])
                            .orderCount((Long) result[4])
                            .quantity((Long) result[5])
                            .revenue((Double) result[6])
                            .build()
            );
        }
        // Return the values as a list
        return new ArrayList<>(insightMonthDtoMap.values());
    }

    public List<BrandInsightDateDto> brandInsightDateDtoMapper(List<Object[]> results, LocalDate from, LocalDate to){
        Map<LocalDate, BrandInsightDateDto> brandInsightDateDtoMap = new LinkedHashMap<>();
        while(from.isBefore(to) || from.isEqual(to)){
            LocalDate date = from;
            brandInsightDateDtoMap.computeIfAbsent(from, d->
                    new BrandInsightDateDto(date, DayOfWeek.from(date), new ArrayList<>()));
            from = from.plusDays(1);
        }
        for(Object[] result: results){
            LocalDate date = (LocalDate) result[0];
            BrandInsightDateDto brandInsight = brandInsightDateDtoMap.computeIfAbsent(date, d->
                            new BrandInsightDateDto(date, DayOfWeek.from(date), new ArrayList<>()));
            brandInsight.getBrandInsights().add(
                BrandInsight.builder()
                        .brandId((Long) result[1])
                        .brandName((String) result[2])
                        .productCount((Long) result[3])
                        .orderCount((Long) result[4])
                        .quantity((Long) result[5])
                        .revenue((Double) result[6])
                        .build());
        }
        return new ArrayList<>(brandInsightDateDtoMap.values());
    }

    public List<BrandInsightMonthDto> brandAnalysisByMonthRange(Long storeId, Integer year, Integer lowerMonth, Integer upperMonth, List<Long> brandIds){
        Store store = storeService.getById(storeId);
        upperMonth = upperMonth == null? 12: upperMonth;
        lowerMonth = lowerMonth == null? 1: lowerMonth;
        List<Brand> brands = (brandIds == null || brandIds.isEmpty())? brandService.getAllBrandsByStore(storeId): brandService.getAllBrandsByStoreAndIds(store, brandIds);
        List<Object[]> results = orderItemRepository.insightsOfBrandsByMonth(store, brands, year, lowerMonth, upperMonth);
        return brandInsightMonthDtoMapper(results, lowerMonth, upperMonth);
    }

    public List<BrandInsightDateDto> brandAnalysisByDateRange(Long storeId, LocalDate from, LocalDate to, List<Long> brandIds){
        Store store = storeService.getById(storeId);
        List<Brand> brands = (brandIds==null || brandIds.isEmpty()) ? brandService.getAllBrandsByStore(storeId) : brandService.getAllBrandsByStoreAndIds(store, brandIds);
        List<Object[]> results = orderItemRepository.insightsOfBrandsByDateRange(store, brands, from, to);
        return brandInsightDateDtoMapper(results, from, to);
    }

    public List<ProductDateDto> analysisOfProductsByDateRange(Long storeId, LocalDate from, LocalDate to){
        Store store = storeService.getById(storeId);

        List<Object[]> results = productRepository.findProductDataByDateRange(store, from, to);

        List<ProductDateDto> productDateDtos = results.stream()
                .map(result -> {
                    return new ProductDateDto((LocalDate) result[0], (Long) result[1], (Float) result[2], (Long) result[3], (Double) result[4], (Brand) result[5]);
                })
                .toList();

        return productDateDtos;
    }

    public List<CategoryDateDto> analysisOfTopProductsByDateRange(Long storeId, LocalDate from, LocalDate to, Integer limit){
        List<Object[]> results = productRepository.findTopProductsByDateRange(storeId, from, to, limit);

        List<CategoryDateDto> productDateDtos = results.stream()
                .map(result -> {
                    LocalDate localDate = ((Date) result[0]).toLocalDate();
                    Brand brand = brandService.getBrandById(storeId, (Long) result[5]);
                    List<Category> categoryList = productService.getByIdAndStore((Long) result[1], storeId).getCategories();
                    return new CategoryDateDto(localDate, (Long) result[1], (Float) result[2], (Long) result[3], ((Float) result[4]).doubleValue(), brand, categoryList);
                })
                .toList();

        return productDateDtos;
    }

    public List<ProductMonthDto> analysisOfProductsByMonths(Long storeId, Integer year, List<Integer> months){
        Store store = storeService.getById(storeId);

        List<Object[]> results = productRepository.findProductsDataByMonth(store, year, months);

        List<ProductMonthDto> productMonthDtos = results.stream()
                .map(result -> {
                    String month = getMonth((Integer) result[0]);
                    return new ProductMonthDto(month, (Long) result[1], (Float) result[2], (Long) result[3], (Double) result[4], (Brand) result[5]);
                })
                .toList();
        return productMonthDtos;
    }

    public List<ProductMonthDto> analysisOfTopProductsByMonths(Long storeId, Integer year, List<Integer> months, Integer limit){
        List<Object[]> results = productRepository.findTopProductsDataByMonth(storeId, year, months, limit);

        List<ProductMonthDto> productMonthDtos = results.stream()
                .map(result -> {
                    String month = getMonth(((Double) result[0]).intValue());
                    Brand brand = brandService.getBrandById(storeId, (Long) result[5]);
                    return new ProductMonthDto(month, (Long) result[1], (Float) result[2], (Long) result[3], ((Float) result[4]).doubleValue(), brand);
                })
                .toList();
        return productMonthDtos;
    }

    public List<CategoryDateDto> analysisOfCategoryWiseSalesByDateRange(Long storeId, LocalDate from, LocalDate to, List<Long> categories){
        Store store = storeService.getById(storeId);
        System.out.println(categories);
        List<Object[]> results = productRepository.findCategorySalesAnalysis(store, from, to, categories, categories.size());

        return results.stream()
                .map(result->{
                    return new CategoryDateDto((LocalDate) result[0], (Long) result[1], (Float) result[2]
                    , (Long) result[3], (Double) result[4], (Brand) result[5], null);
                })
                .toList();
    };

    public List<CategoryMonthDto> analysisOfCategoryWiseSalesByDateRange(Long storeId, Integer year, List<Integer> months, List<Long> categories){
        Store store = storeService.getById(storeId);
        System.out.println(categories);
        List<Object[]> results = productRepository.findCategoryAnalysisByMonth(store, year, months, categories, categories.size());

        return results.stream()
                .map(result->{
                    String month = getMonth((Integer) result[0]);
                    return new CategoryMonthDto(month, (Long) result[1], (Float) result[2]
                    , (Long) result[3], (Double) result[4], (Brand) result[5], null);
                })
                .toList();
    };

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
