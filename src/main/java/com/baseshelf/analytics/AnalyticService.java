package com.baseshelf.analytics;

import com.baseshelf.analytics.dto.*;
import com.baseshelf.brand.Brand;
import com.baseshelf.brand.BrandService;
import com.baseshelf.order.OrderItemRepository;
import com.baseshelf.order.ProductOrderRepository;
import com.baseshelf.store.Store;
import com.baseshelf.store.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class AnalyticService {
    private final StoreService storeService;
    private final ProductOrderRepository productOrderRepository;
    private final BrandService brandService;
    private final OrderItemRepository orderItemRepository;

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
            brandInsight.getAnalysis().add(
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

    private List<ProductInsightMonthDto> productInsightMonthMapper(List<Object[]> results, Integer lowerMonth, Integer upperMonth) {
        Map<Integer, ProductInsightMonthDto> monthDtoLinkedHashMap = new LinkedHashMap<>();

        for(int i = lowerMonth; i<= upperMonth; i++){
            monthDtoLinkedHashMap.computeIfAbsent(i, m ->
                    new ProductInsightMonthDto(m, getMonth(m), new ArrayList<>()));
        }

        for(Object[] result : results){
            Integer month = ((Double) result[0]).intValue();
            ProductInsightMonthDto insightMonthDto = monthDtoLinkedHashMap.computeIfAbsent(month, m ->
                    new ProductInsightMonthDto(m, getMonth(m), new ArrayList<>()));

            insightMonthDto.getAnalysis().add(
                    ProductInsight.builder()
                            .id((Long) result[1])
                            .name((String) result[2])
                            .sellingPrice((Float) result[3])
                            .quantity((Long) result[4])
                            .orderCount((Long) result[5])
                            .revenue((Float) result[6])
                            .brandId((Long) result[7])
                            .brandName((String) result[8])
                            .build()
            );
        }
        return new ArrayList<>(monthDtoLinkedHashMap.values());
    }

    private List<ProductInsightDateDto> productInsightDateMapper(List<Object[]> results, LocalDate from, LocalDate to) {
        Map<LocalDate, ProductInsightDateDto> linkedHashMap = new LinkedHashMap<>();

        while(from.isBefore(to) || from.isEqual(to)){
            linkedHashMap.computeIfAbsent(from, d ->
                    new ProductInsightDateDto(d, DayOfWeek.from(d), new ArrayList<>()));
            from = from.plusDays(1);
        }

        for(Object[] result : results){
            LocalDate date = ((Date) result[0]).toLocalDate();
            ProductInsightDateDto insightDateDto = linkedHashMap.computeIfAbsent(date, d ->
                    new ProductInsightDateDto(d, DayOfWeek.from(d), new ArrayList<>()));

            insightDateDto.getAnalysis().add(
                    ProductInsight.builder()
                            .id((Long) result[1])
                            .name((String) result[2])
                            .sellingPrice((Float) result[3])
                            .quantity((Long) result[4])
                            .orderCount((Long) result[5])
                            .revenue((Float) result[6])
                            .brandId((Long) result[7])
                            .brandName((String) result[8])
                            .build()
            );
        }
        return new ArrayList<>(linkedHashMap.values());
    }

    public List<ProductInsightMonthDto> productAnalysisByMonth(Long storeId, Integer year, Integer lowerMonth, Integer upperMonth, List<Long> productIds, Integer limit){
        Store store = storeService.getById(storeId);
        upperMonth = upperMonth == null? 12: upperMonth;
        lowerMonth = lowerMonth == null? 1: lowerMonth;
        limit = limit == null?50:limit;
        List<Object[]> results = null;
        if(productIds != null){
            results = orderItemRepository.insightsOfProductsMonth(storeId, year, lowerMonth, upperMonth, productIds , limit);
        }
        else{
            results = orderItemRepository.insightsOfProductsMonth(storeId, year, lowerMonth, upperMonth , limit);
        }
        return productInsightMonthMapper(results, lowerMonth, upperMonth);
    }

    public List<ProductInsightDateDto> productAnalysisByDateRange(Long storeId, LocalDate from, LocalDate to, List<Long> productIds, Integer limit){
        List<Object[]> results;
        limit = (limit == null) ? 50 : limit ;
        if(productIds == null || productIds.isEmpty()){
            results = orderItemRepository.insightsOfProductsDate(storeId, from, to, limit);
        }else{
            results = orderItemRepository.insightsOfProductsDate(storeId, from, to, productIds, limit);
        }
        return productInsightDateMapper(results, from, to);
    }

    public List<CategoryInsightMonthDto> categoryInsightMonthMapper(List<Object[]> results, Integer lowerMonth, Integer upperMonth){
        Map<Integer, CategoryInsightMonthDto> linkedHashMap = new LinkedHashMap<>();

        for(int i = lowerMonth; i <= upperMonth; i++){
            linkedHashMap.computeIfAbsent(i, m-> new CategoryInsightMonthDto(m, getMonth(m), new ArrayList<>()));
        }

        for(Object[] result : results){
            Integer month = ((Double) result[0]).intValue();
            CategoryInsightMonthDto dto = linkedHashMap.computeIfAbsent(month,
                    m -> new CategoryInsightMonthDto(m, getMonth(m), new ArrayList<>()));

            dto.getAnalysis().add(
                    CategoryInsight.builder()
                            .id((Long) result[1])
                            .name((String) result[2])
                            .productCount((Long) result[3])
                            .orderCount((Long) result[4])
                            .quantity((Long) result[5])
                            .revenue(((Float) result[6]).doubleValue())
                            .build()
            );
        }
        return new ArrayList<>(linkedHashMap.values());
    }

    private List<CategoryInsightDateDto> categoryInsightDateMapper(List<Object[]> results, LocalDate from, LocalDate to) {
        Map<LocalDate, CategoryInsightDateDto> linkedHashMap = new LinkedHashMap<>();

        while(from.isBefore(to) || from.isEqual(to)){
            linkedHashMap.computeIfAbsent(from, d ->
                    new CategoryInsightDateDto(d, DayOfWeek.from(d), new ArrayList<>()));
            from = from.plusDays(1);
        }

        for(Object[] result : results){
            LocalDate date = ((Date) result[0]).toLocalDate();
            CategoryInsightDateDto insightDateDto = linkedHashMap.computeIfAbsent(date, d ->
                    new CategoryInsightDateDto(d, DayOfWeek.from(d), new ArrayList<>()));

            insightDateDto.getAnalysis().add(
                    CategoryInsight.builder()
                            .id((Long) result[1])
                            .name((String) result[2])
                            .productCount((Long) result[3])
                            .orderCount((Long) result[4])
                            .quantity((Long) result[5])
                            .revenue(((Float) result[6]).doubleValue())
                            .build()
            );
        }
        return new ArrayList<>(linkedHashMap.values());
    }

    public List<CategoryInsightMonthDto> categoryAnalysisByMonth(Long storeId, Integer year, Integer lowerMonth, Integer upperMonth, List<Long> categoryIds, Integer limit){
        Store store = storeService.getById(storeId);
        upperMonth = upperMonth == null? 12: upperMonth;
        lowerMonth = lowerMonth == null? 1: lowerMonth;
        limit = limit == null?50:limit;
        List<Object[]> results = null;

        if(categoryIds == null || categoryIds.isEmpty()){
            results = orderItemRepository.insightsOfCategoryMonth(storeId, year, lowerMonth, upperMonth, limit);
        }else{
            results = orderItemRepository.insightsOfCategoryMonth(storeId, year, lowerMonth, upperMonth, categoryIds, limit);
        }

        return categoryInsightMonthMapper(results, lowerMonth, upperMonth);
    }

    public List<CategoryInsightDateDto> categoryAnalysisByDateRange(Long storeId, LocalDate from, LocalDate to, List<Long> categoryIds, Integer limit){
        List<Object[]> results;
        limit = (limit == null) ? 50 : limit ;
        if(categoryIds == null || categoryIds.isEmpty()){
            results = orderItemRepository.insightsOfCategoryDateRange(storeId, from, to, limit);
        }else{
            results = orderItemRepository.insightsOfCategoryDateRange(storeId, from, to, categoryIds, limit);
        }

        return categoryInsightDateMapper(results, from, to);
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
