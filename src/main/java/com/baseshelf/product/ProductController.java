package com.baseshelf.product;

import com.baseshelf.category.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("products")
public class ProductController {

    private final ProductService productService;

    @GetMapping("no-category-type/{category-type}")
    public List<Product> findProductsWithoutCategory(@PathVariable("category-type") String categoryType){
        return productService.findProductWithoutCategory(categoryType);
    }

    @GetMapping("")
    public List<Product> findAllProducts(){
        return productService.findAllProducts();
    }
}
