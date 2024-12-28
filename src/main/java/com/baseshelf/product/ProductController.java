package com.baseshelf.product;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.output.OutputException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
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
            @RequestParam(value = "category-ids", required = true) List<Long> categoryId
    ){
        return productService.getAllProductsByCategories(storeId, categoryId);
    }

    @GetMapping("categories/without-type")
    public List<Product> getProductsWithoutCategories(
            @PathVariable(name = "store-id") Long storeId,
            @RequestParam(value = "category-type", required = true) String categoryType
    ){
        return productService.getAllProductsWithoutCategories(storeId, categoryType);
    }

    @GetMapping("filters")
    public List<Product> getProductsByFilter(
            @PathVariable(name = "store-id") Long storeId,
            @ModelAttribute @Valid ProductFilter productFilter
    ){
        return productService.getAllProductsByStoreAndFilter(storeId, productFilter);
    }

    @GetMapping("/multiple")
    public List<Product> getProductsByIds(
            @PathVariable(name = "store-id") Long storeId,
            @RequestParam(value = "product-ids") Set<Long> productIds
    ){
        return productService.getAllByStoreAndIds(storeId, productIds);
    }

    @GetMapping("/{product-id}")
    public Product getProductByStoreAndId(
            @PathVariable(name = "store-id") Long storeId,
            @PathVariable(name = "product-id") Long productId
    ){
        return productService.getByIdAndStore(productId, storeId);
    }

    @GetMapping("/{product-id}/barcode")
    public ResponseEntity<byte[]> getProductBarCodeById(
            @PathVariable(name = "store-id") Long storeId,
            @PathVariable(name = "product-id") Long productId
    ) throws OutputException, BarcodeException, IOException {
        return productService.getBarcodeForProduct(storeId, productId);
    }

    @GetMapping("/{product-id}/label")
    public ResponseEntity<byte[]> getProductLabelById(
            @PathVariable(name = "store-id") Long storeId,
            @PathVariable(name = "product-id") Long productId
    ) throws OutputException, BarcodeException, IOException {
        return productService.getProductLabel(storeId, productId);
    }

    @GetMapping("/{product-id}/label-stream")
    public ResponseEntity<StreamingResponseBody> getProductLabelStreamById(
            @PathVariable(name = "store-id") Long storeId,
            @PathVariable(name = "product-id") Long productId
    ) throws OutputException, BarcodeException, IOException {
        return productService.getProductLabelStream(storeId, productId);
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

    @PutMapping("{product-id}/restock")
    public Product restockProductByIdAndQuantity(
            @PathVariable(name = "store-id") Long storeId,
            @PathVariable(name = "product-id") Long productId,
            @RequestBody Integer quantity
    ){
        return productService.restockProduct(storeId, productId, quantity);
    }
}
