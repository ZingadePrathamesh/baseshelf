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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
            Set<Product> products = new HashSet<>();

            for(int i =  0; i< 200; i++){
                Category material = materials.get(faker.number().numberBetween(0, materials.size()));
                Category color = colors.get(faker.number().numberBetween(0, colors.size()));
                Category productT = productType.get(faker.number().numberBetween(0, productType.size()));
                Category size = sizes.get(faker.number().numberBetween(0, sizes.size()));
                List<Category> categories = List.of(material, color, productT, size);

                Product product = Product.builder()
                        .name(color.getName()+ " " + productT.getName())
                        .description("Product with the size " + size.getName())
                        .barcode("random barcode" + i)
                        .categories(categories)
                        .cgst(18.0F)
                        .sgst(18.0F)
                        .costPrice(150F)
                        .sellingPrice(199F)
                        .isTaxed(true)
                        .quantity(faker.number().numberBetween(1, 100))
                        .isActive(true)
                        .brand(brand)
                        .store(store)
                        .build();
                products.add(product);
            }
            productJpaRepository.saveAll(products);
            System.out.println("All products have been saved.");
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
