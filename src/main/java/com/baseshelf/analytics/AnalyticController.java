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

    @GetMapping("/revenue/dates/{date}")
    public List<RevenuePerDate> getRevenueByDate(
            @PathVariable("store-id") Long storeId,
            @PathVariable("date") LocalDate date
    ){
        return analyticService.totalRevenueByDate(storeId, date);
    }

    @GetMapping("/revenue/dates")
    public List<RevenuePerDate> getRevenueByDate(
            @PathVariable("store-id") Long storeId,
            @RequestParam(value = "from") LocalDate from,
            @RequestParam(value = "to") LocalDate to
    ){
        return analyticService.totalRevenueByDateRange(storeId, from, to);
    }

    @GetMapping("revenue/months")
    public List<RevenueMonthDto> getRevenueByMonth(
            @PathVariable("store-id") Long storeId,
            @RequestParam(value = "year") Integer year,
            @RequestParam(value = "months") List<Integer> months
    ){
                return analyticService.totalRevenueByYearAndMonth(storeId, year, months);
    }

}
