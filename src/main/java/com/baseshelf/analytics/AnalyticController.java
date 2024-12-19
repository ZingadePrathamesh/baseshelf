package com.baseshelf.analytics;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("stores/{store-id}/analytics")
public class AnalyticController {
    private final AnalyticService analyticService;

    @GetMapping("/revenue/dates/{date}")
    public Map<LocalDate, Double> getRevenueByDate(
            @PathVariable("store-id") Long storeId,
            @PathVariable("date") LocalDate date
    ){
        return analyticService.totalRevenueByDate(storeId, date);
    }

    @GetMapping("/revenue/dates")
    public Map<LocalDate, Double> getRevenueByDate(
            @PathVariable("store-id") Long storeId,
            @RequestParam(required = false, value = "from") LocalDate from,
            @RequestParam(required = false, value = "to") LocalDate to
    ){
        return analyticService.totalRevenueByDateRange(storeId, from, to);
    }

    @GetMapping("revenue/months")
    public List<RevenueMonthDto> getRevenueByMonth(
            @PathVariable("store-id") Long storeId,
            @RequestParam(required = true, value = "year") Integer year,
            @RequestParam(required = true, value = "months") List<Integer> months
    ){
                return analyticService.totalRevenueByYearAndMonth(storeId, year, months);
    }



}
