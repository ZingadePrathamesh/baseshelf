package com.baseshelf.product;

import com.baseshelf.brand.Brand;
import com.baseshelf.brand.BrandService;
import com.baseshelf.category.Category;
import com.baseshelf.category.CategoryService;
import com.baseshelf.store.Store;
import com.baseshelf.store.StoreService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductJpaRepository productJpaRepository;
    private final StoreService storeService;
    private final BrandService brandService;
    private final CategoryService categoryService;

    @Bean
    @Order(value = 4)
    public CommandLineRunner insertProducts(
            ProductJpaRepository productJpaRepository,
            StoreService storeService,
            BrandService brandService,
            CategoryService categoryService
    ){
        return args -> {
            Faker faker = new Faker();
            Store store = storeService.getByEmail("johndoe@gmail.com");
            Brand brand = brandService.getAllBrandsByStore(store.getId()).getFirst();
            List<Category> materials = categoryService.getAllByIdOrNameOrCategoryType(store.getId(), null, null, "MATERIAL");
            List<Category> colors = categoryService.getAllByIdOrNameOrCategoryType(store.getId(), null, null, "COLOR");
            List<Category> productType = categoryService.getAllByIdOrNameOrCategoryType(store.getId(), null, null, "PRODUCT TYPE");
            List<Category> sizes = categoryService.getAllByIdOrNameOrCategoryType(store.getId(), null, null, "SIZE");

        };
    }

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
