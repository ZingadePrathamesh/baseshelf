package com.baseshelf.brand;

import com.baseshelf.store.Store;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("stores/store-id/{store-id}/brands")
public class BrandController {

    private final BrandService brandService;

    @GetMapping("")
    public List<Brand> getAllBrandsByStore(@PathVariable("store-id") Long storeId,
                                           @RequestParam(required = false, name = "sorted") boolean sorted){
        return brandService.getAllBrandsByStore(storeId);
    }

    @GetMapping("/sorted")
    public List<Brand> getAllBrandsByStoreDesc(@PathVariable("store-id") Long storeId){
        return brandService.sortByProductCount(storeId);
    }

    @PostMapping("")
    public Brand createBrand(
            @PathVariable("store-id") Long storeId,
            @Valid @RequestBody Brand brand
    ){
        return brandService.registerBrand(storeId, brand);
    }

    @GetMapping("brand-id/{brand-id}")
    public Brand getBrandById(
            @PathVariable("store-id") Long storeId,
            @PathVariable("brand-id") Long brandId
                                        ){
        return brandService.getBrandById(storeId, brandId);
    }

    @GetMapping("/count")
    public ResponseEntity<Object> getBrandCountByStore(
            @PathVariable("store-id") Long storeId
    ){
        return brandService.getBrandCountByStore(storeId);
    }

    @GetMapping("product-id/{product-id}")
    public Brand getBrandByProductId(
            @PathVariable("store-id") Long storeId,
            @PathVariable("product-id") Long productId
    ){
        return brandService.getBrandByProduct(storeId, productId);
    }

    @PutMapping("update/brand-id/{brand-id}")
    public Brand updateBrandById(
            @PathVariable("store-id") Long storeId,
            @PathVariable("brand-id") Long brandId,
            @Valid @RequestBody BrandDto brandDto
    ){
        return brandService.updateBrandById(storeId, brandId, brandDto);
    }

    @DeleteMapping("delete/brand-id/{brand-id}")
    public void deleteBrandById(
            @PathVariable("store-id") Long storeId,
            @PathVariable("brand-id") Long brandId
    ){
        brandService.deleteBrandById(storeId, brandId);
    }
}
