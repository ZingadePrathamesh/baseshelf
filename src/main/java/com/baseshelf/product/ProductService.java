package com.baseshelf.product;

import com.baseshelf.brand.Brand;
import com.baseshelf.brand.BrandService;
import com.baseshelf.category.Category;
import com.baseshelf.category.CategoryService;
import com.baseshelf.store.Store;
import com.baseshelf.store.StoreService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductJpaRepository productJpaRepository;
    private final StoreService storeService;
    private final BrandService brandService;

    public Product getByIdAndStore(Long productId, Store store) {
        Optional<Product> productOptional = productJpaRepository.findByIdAndStore(productId, store);
        return productOptional.orElseThrow(()->
                new ProductNotFoundException("Product with id: " + productId + " does not exist."));
    }

    public List<Product> getAllProductsFromStore(Long storeId){
        Store store =  storeService.getById(storeId);
        return productJpaRepository.findAllByStore(store);
    }

    public List<Product> getAllProductsFromStoreAndBrand(Long storeId, Long brandId){
        Store store = storeService.getById(storeId);
        Brand brand = brandService.getBrandById(storeId, brandId);
        return productJpaRepository.findAllByStoreAndBrand(store, brand);
    }

    public List<Product> getAllProductsByCategories(List<Long> categoryIds){
        long categoryCount = (long) categoryIds.size();
        List<Product> products = productJpaRepository.findByAllCategoryIds(categoryIds, categoryCount);
        return products;
    }


}
