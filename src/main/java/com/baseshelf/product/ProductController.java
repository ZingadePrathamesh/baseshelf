package com.baseshelf.product;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

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
        return productService.getAllProductsByStoreAndFilter(storeId, productFilter);
    }

    @GetMapping("/multiple")
    public List<Product> getProductsByIds(
            @PathVariable(name = "store-id") Long storeId,
            @RequestBody Set<Long> productIds
    ){

        return productService.getAllByStoreAndIds(storeId, productIds);
    }

    @GetMapping("products/{product-id}")
    public Product getProductByStoreAndId(
            @PathVariable(name = "store-id") Long storeId,
            @PathVariable(name = "product-id") Long productId
    ){
        return productService.getByIdAndStore(productId, storeId);
    }

    @PostMapping("")
    public Product saveNewProduct(
            @PathVariable(name = "store-id") Long storeId,
            @Valid @RequestBody Product product
    ){
        return productService.saveProduct(storeId, product);
    }

    @DeleteMapping("delete")
    public void deleteProductByStoreAndIds(
            @PathVariable(name = "store-id") Long storeId,
            @RequestParam(required = false, name = "product-ids") List<Long> productIds
    ){
        productService.deleteById(storeId, productIds);
    }

    @PutMapping("{product-id}")
    public Product updateProductByStoreAndId(
            @PathVariable(name = "store-id") Long storeId,
            @PathVariable(name = "product-id") Long productId,
            @Valid @RequestBody Product product
    ){
        return productService.updateProductByStoreAndId(storeId, productId, product);
    }

    @PutMapping("{product-id}/sell")
    public Product sellProduct(
            @PathVariable(name = "store-id") Long storeId,
            @PathVariable(name = "product-id") Long productId,
            @RequestParam(required = true, name = "quantity") Integer quantity
    ){
        return productService.sellProductByProductId(storeId, productId, quantity);
    }
}
