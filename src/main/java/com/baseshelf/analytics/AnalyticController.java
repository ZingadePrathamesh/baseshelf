package com.baseshelf.analytics;

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
    public List<RevenuePerDate> getRevenueByDate(
            @PathVariable("store-id") Long storeId,
            @PathVariable("date") LocalDate date
    ){
        return analyticService.getRevenueByDate(storeId, date);
    }

    @GetMapping("/orders/date-range")
    public List<RevenuePerDate> getRevenueByDate(
            @PathVariable("store-id") Long storeId,
            @RequestParam(value = "from") LocalDate from,
            @RequestParam(value = "to") LocalDate to
    ){
        return analyticService.getRevenueByDateRange(storeId, from, to);
    }

    @GetMapping("orders/year-months")
    public List<RevenueMonthDto> getRevenueByMonth(
            @PathVariable("store-id") Long storeId,
            @RequestParam(value = "year") Integer year,
            @RequestParam(value = "months") List<Integer> months
    ){
                return analyticService.getRevenueForMonthsAndYear(storeId, year, months);
    }

    @GetMapping("brands/{brand-id}/months")
    public List<AnalyticsByBrand> getAnalysisForBrandByYearAndMonth(
            @PathVariable("store-id") Long storeId,
            @PathVariable("brand-id") Long brandId,
            @RequestParam(value = "year") Integer year,
            @RequestParam(value = "months") List<Integer> months
    ){
        return analyticService.totalAnalysisByBrandByYearAndMonth(storeId, brandId, year, months);
    }

    @GetMapping("brands/{brand-id}/date-range")
    public List<AnalyticsByBrandDate> getAnalysisForBrandByDateRange(
            @PathVariable("store-id") Long storeId,
            @PathVariable("brand-id") Long brandId,
            @RequestParam(value = "from") LocalDate from,
            @RequestParam(value = "to") LocalDate to
    ){
        return analyticService.totalAnalysisOfBrandByDateRange(storeId, brandId, from, to);
    }

    @GetMapping("multi-brands/date-range")
    public List<AnalyticsByBrandDate> getAnalysisForBrandByDateRange(
            @PathVariable("store-id") Long storeId,
            @RequestParam("brand-ids") List<Long> brandIds,
            @RequestParam(value = "from") LocalDate from,
            @RequestParam(value = "to") LocalDate to
    ){
        return analyticService.totalAnalysisOfMultipleBrandsByDateRange(storeId, brandIds, from, to);
    }


}
