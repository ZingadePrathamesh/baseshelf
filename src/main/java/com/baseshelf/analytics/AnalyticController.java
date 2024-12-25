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

    @GetMapping("products/date-range/new")
    public List<ProductInsightDateDto> getProductsAnalysisByDateRange(
            @PathVariable("store-id") Long storeId,
            @RequestParam(required = true, value = "from") LocalDate from,
            @RequestParam(required = true, value= "to") LocalDate to,
            @RequestParam(required = false, value= "product-ids") List<Long> productIds,
            @RequestParam(required = false, value = "limit") Integer limit
    ){
        return analyticService.productAnalysisByDateRange(storeId, from, to, productIds, limit);
    }

    @GetMapping("products/months/new")
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

    @GetMapping("products/date-range")
    public List<ProductDateDto> getProductDataByDateRange(
            @PathVariable("store-id") Long storeId,
            @RequestParam(value = "from") LocalDate from,
            @RequestParam(value = "to") LocalDate to
    ){
        return analyticService.analysisOfProductsByDateRange(storeId, from, to);
    }

    @GetMapping("products/date-range/top")
    public List<CategoryDateDto> getProductDataByDateRange(
            @PathVariable("store-id") Long storeId,
            @RequestParam(value = "from") LocalDate from,
            @RequestParam(value = "to") LocalDate to,
            @RequestParam(value = "top") Integer top
    ){
        return analyticService.analysisOfTopProductsByDateRange(storeId, from, to, top);
    }

    @GetMapping("products/months")
    public List<ProductMonthDto> getProductDataByMonths(
            @PathVariable("store-id") Long storeId,
            @RequestParam(value = "year") Integer year,
            @RequestParam(value = "months") List<Integer> months
    ){
        return analyticService.analysisOfProductsByMonths(storeId, year, months);
    }

    @GetMapping("products/months/top")
    public List<ProductMonthDto> getTopProductDataByMonths(
            @PathVariable("store-id") Long storeId,
            @RequestParam(value = "year") Integer year,
            @RequestParam(value = "months") List<Integer> months,
            @RequestParam(value = "top") Integer top
    ){
        return analyticService.analysisOfTopProductsByMonths(storeId, year, months, top);
    }

    @GetMapping("categories/date-range")
    public List<CategoryDateDto> getProductDataByDateRange(
            @PathVariable("store-id") Long storeId,
            @RequestParam(value = "from") LocalDate from,
            @RequestParam(value = "to") LocalDate to,
            @RequestParam(value = "categories") List<Long> categories
    ){
        return analyticService.analysisOfCategoryWiseSalesByDateRange(storeId, from, to, categories);
    }

    @GetMapping("categories/months")
    public List<CategoryMonthDto> getTopProductDataByMonths(
            @PathVariable("store-id") Long storeId,
            @RequestParam(value = "year") Integer year,
            @RequestParam(value = "months") List<Integer> months,
            @RequestParam(value = "categories") List<Long> categories
    ){
        return analyticService.analysisOfCategoryWiseSalesByDateRange(storeId, year, months, categories);
    }


}
