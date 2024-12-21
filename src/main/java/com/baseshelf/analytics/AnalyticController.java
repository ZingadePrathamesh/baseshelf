package com.baseshelf.analytics;

import com.baseshelf.analytics.dto.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @GetMapping("brands/{brand-id}/months")
    public List<BrandMonthDto> getAnalysisForBrandByYearAndMonth(
            @PathVariable("store-id") Long storeId,
            @PathVariable("brand-id") Long brandId,
            @RequestParam(value = "year") Integer year,
            @RequestParam(value = "months") List<Integer> months
    ){
        return analyticService.totalAnalysisByBrandByYearAndMonth(storeId, brandId, year, months);
    }

    @GetMapping("multi-brands/months")
    public List<BrandMonthDto> getAnalysisForMultiBrandByYearAndMonth(
            @PathVariable("store-id") Long storeId,
            @RequestParam(value = "brand-ids") List<Long> brandIds,
            @RequestParam(value = "year") Integer year,
            @RequestParam(value = "months") List<Integer> months
    ){
        return analyticService.totalAnalysisOfMultipleBrandsByMonthYear(storeId, brandIds, year, months);
    }

    @GetMapping("brands/{brand-id}/date-range")
    public List<BrandDateDto> getAnalysisForBrandByDateRange(
            @PathVariable("store-id") Long storeId,
            @PathVariable("brand-id") Long brandId,
            @RequestParam(value = "from") LocalDate from,
            @RequestParam(value = "to") LocalDate to
    ){
        return analyticService.totalAnalysisOfBrandByDateRange(storeId, brandId, from, to);
    }

    @GetMapping("multi-brands/date-range")
    public List<BrandDateDto> getAnalysisForBrandByDateRange(
            @PathVariable("store-id") Long storeId,
            @RequestParam(value = "brand-ids") List<Long> brandIds,
            @RequestParam(value = "from") LocalDate from,
            @RequestParam(value = "to") LocalDate to
    ){
        return analyticService.totalAnalysisOfMultipleBrandsByDateRange(storeId, brandIds, from, to);
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
    public List<ProductDateDto> getProductDataByDateRange(
            @PathVariable("store-id") Long storeId,
            @RequestParam(value = "from") LocalDate from,
            @RequestParam(value = "to") LocalDate to,
            @RequestParam(value = "top") Integer top
    ){
        return analyticService.analysisOfTopProductsByDateRange(storeId, from, to, top);
    }




}
