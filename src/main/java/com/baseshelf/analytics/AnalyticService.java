package com.baseshelf.analytics;

import com.baseshelf.brand.Brand;
import com.baseshelf.brand.BrandService;
import com.baseshelf.order.OrderItemRepository;
import com.baseshelf.order.ProductOrderRepository;
import com.baseshelf.store.Store;
import com.baseshelf.store.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class AnalyticService {
    private final StoreService storeService;
    private final ProductOrderRepository productOrderRepository;
    private final BrandService brandService;
    private final OrderItemRepository orderItemRepository;

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
        if (storeId == null || year == null || months == null || months.isEmpty()) {
            throw new IllegalArgumentException("Invalid input: storeId, year, and months must be non-null, and months must not be empty.");
        }

        Store store = storeService.getById(storeId);
        List<Object[]> results = productOrderRepository.findRevenueByMonthAndYear(store, year, months);
        List<RevenueMonthDto> revenueMonthDtos = results.stream()
                .map(result-> new RevenueMonthDto(getMonth((Integer) result[0]), (Integer) result[0], (Long) result[1] , (Double) result[2], null))
                .toList();

        IntStream.range(0, revenueMonthDtos.size())
                .forEach( i ->{
                    if (i == 0) revenueMonthDtos.get(i).setGrowthPercentage(0D);
                    else{
                        double previousMonthRevenue = revenueMonthDtos.get(i-1).getRevenue();
                        double currentMonthRevenue = revenueMonthDtos.get(i).getRevenue();
                        double profitPercent = previousMonthRevenue == 0 ? 0 : (currentMonthRevenue/previousMonthRevenue - 1) * 100;
                        revenueMonthDtos.get(i).setGrowthPercentage(profitPercent);
                    }
                });
        return revenueMonthDtos;
    }

    public List<AnalyticsByBrand> totalAnalysisByBrandByYearAndMonth(Long storeId, Long brandId, Integer year, List<Integer> months) {
        Store store = storeService.getById(storeId);
        Brand brand = brandService.getBrandById(store, brandId);
        List<Object[]> results = orderItemRepository.analysisByBrandByYearAndMonth(store, brand, year, months);
        List<AnalyticsByBrand> analytics = results.stream()
                .map(result -> new AnalyticsByBrand(getMonth((Integer) result[1]), (Long) result[2], (Long) result[3], (Double) result[4], null))
                .toList();
        return analytics;
    }

    public List<AnalyticsByBrandDate> totalAnalysisOfBrandByDateRange(Long storeId, Long brandId, LocalDate from, LocalDate to){
        Store store = storeService.getById(storeId);
        Brand brand = brandService.getBrandById(store, brandId);

        List<Object[]> results = orderItemRepository.analysisOfBrandByDateRange(store, brand, from, to);

        List<AnalyticsByBrandDate> analytics = results.stream()
                .map(result ->{
                    LocalDate date = (LocalDate) result[0];
                    String weekDay = DayOfWeek.from(date).name();
                    return new AnalyticsByBrandDate(brandId, (LocalDate) result[0], (Long) result[1], (Long) result[2], (Double) result[3], weekDay);
                })
                .toList();

        return analytics;
    }

    public List<AnalyticsByBrandDate> totalAnalysisOfMultipleBrandsByDateRange(Long storeId, List<Long> brandIds, LocalDate from, LocalDate to){
        Store store = storeService.getById(storeId);
        List<Brand> brands = brandService.getAllBrandsByStoreAndIds(store, brandIds);

        List<Object[]> results = orderItemRepository.analysisOfMultipleBrandsByDateRange(store, brands, from, to);

        List<AnalyticsByBrandDate> analytics = results.stream()
                .map(result ->{
                    LocalDate date = (LocalDate) result[1];
                    String weekDay = DayOfWeek.from(date).name();
                    return new AnalyticsByBrandDate((Long) result[0], (LocalDate) result[1], (Long) result[2], (Long) result[3], (Double) result[4], weekDay);
                })
                .toList();

        return analytics;
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
