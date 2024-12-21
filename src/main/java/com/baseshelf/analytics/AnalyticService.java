package com.baseshelf.analytics;

import com.baseshelf.analytics.dto.*;
import com.baseshelf.brand.Brand;
import com.baseshelf.brand.BrandService;
import com.baseshelf.category.Category;
import com.baseshelf.order.OrderItemRepository;
import com.baseshelf.order.ProductOrderRepository;
import com.baseshelf.product.ProductRepository;
import com.baseshelf.store.Store;
import com.baseshelf.store.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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

    public List<BrandMonthDto> totalAnalysisByBrandByYearAndMonth(Long storeId, Long brandId, Integer year, List<Integer> months) {
        Store store = storeService.getById(storeId);
        Brand brand = brandService.getBrandById(store, brandId);
        List<Object[]> results = orderItemRepository.analysisByBrandByYearAndMonth(store, brand, year, months);
        List<BrandMonthDto> analytics = results.stream()
                .map(result -> {
                    String month = getMonth((Integer) result[1]);
                    return new BrandMonthDto((Long) result[0], month, (Long) result[2], (Long) result[3], (Long) result[4], (Double) result[5], null);
                })
                .toList();
        calculateRevenue(analytics);
        return analytics;
    }

    public List<BrandDateDto> totalAnalysisOfBrandByDateRange(Long storeId, Long brandId, LocalDate from, LocalDate to){
        Store store = storeService.getById(storeId);
        Brand brand = brandService.getBrandById(store, brandId);

        List<Object[]> results = orderItemRepository.analysisOfBrandByDateRange(store, brand, from, to);

        List<BrandDateDto> analytics = results.stream()
                .map(result ->{
                    LocalDate date = (LocalDate) result[0];
                    String weekDay = DayOfWeek.from(date).name();
                    return new BrandDateDto(brandId, (LocalDate) result[0], (Long) result[1], (Long) result[2], (Double) result[3], weekDay);
                })
                .toList();

        return analytics;
    }

    public List<BrandDateDto> totalAnalysisOfMultipleBrandsByDateRange(Long storeId, List<Long> brandIds, LocalDate from, LocalDate to){
        Store store = storeService.getById(storeId);
        List<Brand> brands = brandService.getAllBrandsByStoreAndIds(store, brandIds);

        List<Object[]> results = orderItemRepository.analysisOfMultipleBrandsByDateRange(store, brands, from, to);

        List<BrandDateDto> analytics = results.stream()
                .map(result ->{
                    LocalDate date = (LocalDate) result[1];
                    String weekDay = DayOfWeek.from(date).name();
                    return new BrandDateDto((Long) result[0], (LocalDate) result[1], (Long) result[2], (Long) result[3], (Double) result[4], weekDay);
                })
                .toList();

        return analytics;
    }

    public List<BrandMonthDto> totalAnalysisOfMultipleBrandsByMonthYear(Long storeId, List<Long> brandIds, Integer year, List<Integer> months){
        Store store = storeService.getById(storeId);
        List<Brand> brands = brandService.getAllBrandsByStoreAndIds(store, brandIds);

        List<Object[]> results = orderItemRepository.analysisOfMultipleBrandsByMonthYear(store, brands, year, months);

        List<BrandMonthDto> analytics = results.stream()
                .map(result ->{
                    String month = getMonth((Integer) result[1]);
                    return new BrandMonthDto((Long) result[0], month, (Long) result[2], (Long) result[3], (Long) result[4], (Double) result[5], null);
                })
                .toList();
        calculateRevenue(analytics);
        return analytics;
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

    public List<ProductDateDto> analysisOfTopProductsByDateRange(Long storeId, LocalDate from, LocalDate to, Integer limit){
        List<Object[]> results = productRepository.findTopProductsByDateRange(storeId, from, to, limit);

        List<ProductDateDto> productDateDtos = results.stream()
                .map(result -> {
                    LocalDate localDate = ((Date) result[0]).toLocalDate();
                    Brand brand = brandService.getBrandById(storeId, (Long) result[5]);
                    return new ProductDateDto(localDate, (Long) result[1], (Float) result[2], (Long) result[3], ((Float) result[4]).doubleValue(), brand);
                })
                .toList();

        return productDateDtos;
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

    private void calculateRevenue(List<BrandMonthDto> analytics) {
        IntStream.range(0, analytics.size())
                .forEach(i->{
                    if(i==0){
                        analytics.get(i).setProfitPercent(0D);
                    }
                    else{
                        double previousMonthRevenue = analytics.get(i-1).getRevenue();
                        double currentMonthRevenue = analytics.get(i).getRevenue();
                        double profitPercent = previousMonthRevenue == 0 ? 0 : (currentMonthRevenue/previousMonthRevenue - 1) * 100;
                        analytics.get(i).setProfitPercent(profitPercent);
                    }
                });
    }

}
