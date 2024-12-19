package com.baseshelf.analytics;

import com.baseshelf.brand.BrandService;
import com.baseshelf.category.CategoryService;
import com.baseshelf.order.ProductOrderRepository;
import com.baseshelf.order.ProductOrderService;
import com.baseshelf.product.ProductService;
import com.baseshelf.store.StoreService;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticService {
    private final ProductService productService;
    private final StoreService storeService;
    private final ProductOrderService productOrderService;
    private final BrandService brandService;
    private final CategoryService categoryService;

    public Map<LocalDate, Double> totalRevenueByDate(Long storeId, LocalDate date){
        Double totalRevenue = productOrderService.getRevenueByDate(storeId, date);
        Map<LocalDate, Double> map =  new HashMap<>();
        map.put(date, totalRevenue);
        return map;
    }

    public Map<LocalDate, Double> totalRevenueByDateRange(Long storeId, LocalDate from, LocalDate to){
        Map<LocalDate, Double> map = productOrderService.getRevenueByDateRange(storeId, from, to);
        return map;
    }

    public List<RevenueMonthDto> totalRevenueByYearAndMonth(Long storeId, @Size(min = 1000, max = 9999, message = "year should be between 1000 and 9999.") Integer year, List<Integer> months) {
        return productOrderService.getRevenueForMonthsAndYear(storeId, year, months);
    }
}
