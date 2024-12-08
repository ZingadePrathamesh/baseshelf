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

    @GetMapping("by-category")
    public List<Product> getProductsByCategories(
            @RequestBody List<Long> categoryId
    ){
        return productService.getAllProductsByCategories(categoryId);
    }
}
