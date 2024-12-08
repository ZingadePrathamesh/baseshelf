package com.baseshelf.product;

import com.baseshelf.category.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("stores/{store-id}/products")
public class ProductController {
    private final ProductService productService;

    @GetMapping("")
    public List<Product> getAllByStore(
            @PathVariable(name = "store-id") Long storeId
    ){
        return productService.getAllProductsFromStore(storeId);
    }

    @GetMapping("/brands/{brand-id}")
    public List<Product> getAllByBrand(
            @PathVariable(name = "store-id") Long storeId,
            @PathVariable(name = "brand-id") Long brandId
    ){
        return productService.getAllProductsFromStoreAndBrand(storeId, brandId);
    }

    @GetMapping("categories")
    public List<Product> getProductsByCategories(
            @PathVariable(name = "store-id") Long storeId,
            @RequestBody List<Long> categoryId
    ){
        return productService.getAllProductsByCategories(storeId, categoryId);
    }

    @GetMapping("filters")
    public List<Product> getProductsByFilter(
            @PathVariable(name = "store-id") Long storeId,
            @ModelAttribute ProductFilter productFilter
    ){
        System.out.println(productFilter.getName());
        System.out.println(productFilter.getCategoryIds());
        return productService.getAllProductsByStoreAndFilter(storeId, productFilter);
    }
}
