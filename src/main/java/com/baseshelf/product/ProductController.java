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
}
