package com.baseshelf.analytics;

import com.baseshelf.analytics.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("stores/{store-id}/analytics")
public class AnalyticController {
    private final AnalyticService analyticService;

    @GetMapping("/orders/dates/{date}")
    public List<OrderDateDto> getRevenueByDate(
            @PathVariable("store-id") Long storeId,
            @PathVariable("date") LocalDate date
    ){
        return analyticService.getRevenueByDate(storeId, date);
    }

    @GetMapping("/orders/date-range")
    public List<OrderDateDto> getRevenueByDate(
            @PathVariable("store-id") Long storeId,
            @RequestParam(value = "from") LocalDate from,
            @RequestParam(value = "to") LocalDate to
    ){
        return analyticService.getRevenueByDateRange(storeId, from, to);
    }

    @GetMapping("orders/year-months")
    public List<OrderMonthDto> getRevenueByMonth(
            @PathVariable("store-id") Long storeId,
            @RequestParam(value = "year") Integer year,
            @RequestParam(value = "months") List<Integer> months
    ){
                return analyticService.getRevenueForMonthsAndYear(storeId, year, months);
    }

    @GetMapping("brands/months")
    public List<BrandInsightMonthDto> getBrandAnalysisByMonths(
            @PathVariable("store-id") Long storeId,
            @RequestParam(required = true, value = "year") Integer year,
            @RequestParam(required = false, value= "lower-month") Integer lowerMonth,
            @RequestParam(required = false, value= "upper-month") Integer upperMonth,
            @RequestParam(required = false, value= "brand-ids") List<Long> brandIds
    ){
        return analyticService.brandAnalysisByMonthRange(storeId, year, lowerMonth, upperMonth, brandIds);
    }

    @GetMapping("brands/date-range")
    public List<BrandInsightDateDto> getBrandAnalysisByDateRange(
            @PathVariable("store-id") Long storeId,
            @RequestParam(required = true, value = "from") LocalDate from,
            @RequestParam(required = true, value= "to") LocalDate to,
            @RequestParam(required = false, value= "brand-ids") List<Long> brandIds
    ){
        return analyticService.brandAnalysisByDateRange(storeId, from, to, brandIds);
    }

    @GetMapping("products/date-range")
    public List<ProductInsightDateDto> getProductsAnalysisByDateRange(
            @PathVariable("store-id") Long storeId,
            @RequestParam(required = true, value = "from") LocalDate from,
            @RequestParam(required = true, value= "to") LocalDate to,
            @RequestParam(required = false, value= "product-ids") List<Long> productIds,
            @RequestParam(required = false, value = "limit") Integer limit
    ){
        return analyticService.productAnalysisByDateRange(storeId, from, to, productIds, limit);
    }

    @GetMapping("products/months")
    public List<ProductInsightMonthDto> getProductsAnalysisByMonths(
            @PathVariable("store-id") Long storeId,
            @RequestParam(required = true, value = "year") Integer year,
            @RequestParam(required = false, value= "lower-month") Integer lowerMonth,
            @RequestParam(required = false, value= "upper-month") Integer upperMonth,
            @RequestParam(required = false, value= "product-ids") List<Long> productIds,
            @RequestParam(required = false, value = "limit") Integer limit
    ){
        return analyticService.productAnalysisByMonth(storeId, year, lowerMonth, upperMonth, productIds, limit);
    }

    @GetMapping("categories/months")
    public List<CategoryInsightMonthDto> getProductAnalysisByMonths(
            @PathVariable("store-id") Long storeId,
            @RequestParam(value = "year", required = true) Integer year,
            @RequestParam(value = "lower-month", required = false) Integer lowerMonth,
            @RequestParam(value = "upper-month", required = false) Integer upperMonth,
            @RequestParam(value = "category-ids", required = false) List<Long> categoryIds,
            @RequestParam(value = "limit", required = false) Integer limit
    ){
        return analyticService.categoryAnalysisByMonth(storeId, year, lowerMonth, upperMonth, categoryIds, limit);
    }

    @GetMapping("categories/date-range")
    public List<CategoryInsightDateDto> getProductAnalysisByDateRange(
            @PathVariable("store-id") Long storeId,
            @RequestParam(value = "from") LocalDate from,
            @RequestParam(value = "to") LocalDate to,
            @RequestParam(value = "category-ids", required = false) List<Long> categoryIds,
            @RequestParam(value = "limit", required = false) Integer limit
    ){
        return analyticService.categoryAnalysisByDateRange(storeId, from, to, categoryIds, limit);
    }

}
