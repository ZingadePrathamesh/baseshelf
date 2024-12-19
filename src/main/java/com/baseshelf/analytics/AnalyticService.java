package com.baseshelf.analytics;

import com.baseshelf.brand.BrandService;
import com.baseshelf.category.CategoryService;
import com.baseshelf.order.ProductOrderRepository;
import com.baseshelf.order.ProductOrderService;
import com.baseshelf.product.ProductService;
import com.baseshelf.store.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnalyticService {
    private final ProductService productService;
    private final StoreService storeService;
    private final ProductOrderService productOrderService;
    private final BrandService brandService;
    private final CategoryService categoryService;


}
