package com.baseshelf.brand;

import com.baseshelf.product.Product;
import com.baseshelf.product.ProductService;
import com.baseshelf.store.Store;
import com.baseshelf.store.StoreService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BrandService {
    private final BrandRepository brandRepository;
    private final StoreService storeService;
    private final ProductService productService;
    @Bean
    @Order(value = 2)
    public CommandLineRunner insertBrand(
            BrandRepository brandRepository, StoreService storeService
    ){
        return args -> {
            Faker faker = new Faker();
            Set<Brand> brands = new HashSet<>();
            Store store = storeService.getById(1L);
            for (int i = 0; i< 10; i++){
                String name = faker.brand().watch();
                Brand brand = Brand.builder()
                        .name(name)
                        .description("Name of the brand is "+ name)
                        .store(store)
                        .build();
                brands.add(brand);
            }
            brandRepository.saveAll(brands);
        };
    }

    public List<Brand> getAllBrandsByStore(Long storeId) {
        Store store = storeService.getById(storeId);
       return brandRepository.findAllByStore(store);
    }

    public Brand registerBrand(Long storeId, Brand brand) {
        Store store=  storeService.getById(storeId);
        Optional<Brand> brandOptional = brandRepository.findByStoreAndName(store, brand.getName());
        brand.setStore(store);
        return brandOptional.orElseGet(() -> brandRepository.save(brand));
    }

    public Brand getBrandById(Long storeId, Long brandId) {
        Store store = storeService.getById(storeId);

        Optional<Brand> byStoreAndId = brandRepository.findByStoreAndId(store, brandId);

        return byStoreAndId.orElseThrow(()->
                new BrandNotFoundException("Brand with id "+ brandId + " does not exists")
        );
    }

    public ResponseEntity<Object> getBrandCountByStore(Long storeId) {
        Map<String, Long> response = new HashMap<>();
        Store store = storeService.getById(storeId);
        Long count = brandRepository.countByStore(store);
        response.put("Count", count);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public Brand getBrandByProduct(Long storeId, Long productId) {
        Store store = storeService.getById(storeId);
        Product product = productService.getByIdAndStore(productId, store);
        Brand brand = product.getBrand();
        if(brand == null){
           throw new BrandNotFoundException("Brand does not exist for product id: " + productId);
        }
        else return brand;
    }

    @Transactional
    public Brand updateBrandById(Long storeId, Long brandId, @Valid BrandDto brandDto) {
        Brand brand = this.getBrandById(storeId, brandId);
        brand.setName(brandDto.getName());
        brand.setDescription(brandDto.getDescription());
        brand.setLastModifiedOn(LocalDate.now());
        return brand;
    }

    public void deleteBrandById(Long storeId, Long brandId) {
        Brand brand = this.getBrandById(storeId, brandId);
        brandRepository.delete(brand);
    }

    public List<Brand> sortByProductCount(Long storeId){
        Store store = storeService.getById(storeId);
        List<Brand> brands = brandRepository.findAllByStoreOrderByProductsDesc(store);
        return brands;
    }
}
