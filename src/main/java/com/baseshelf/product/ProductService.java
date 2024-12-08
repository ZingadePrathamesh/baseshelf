package com.baseshelf.product;

import com.baseshelf.brand.Brand;
import com.baseshelf.brand.BrandService;
import com.baseshelf.category.Category;
import com.baseshelf.category.CategoryService;
import com.baseshelf.store.Store;
import com.baseshelf.store.StoreNotFoundException;
import com.baseshelf.store.StoreService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.domain.Specification;
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

    public List<Product> getAllProductsByCategories(Long storeId, List<Long> categoryIds){
        Store store = storeService.getById(storeId);

        long categoryCount = (long) categoryIds.size();
        List<Product> products = productJpaRepository.findByAllCategoryIdsAndStore(store, categoryIds, categoryCount);
        return products;
    }

    public List<Product> getAllProductsByStoreAndFilter(Long storeId, ProductFilter productFilter) {
        Store store = storeService.getById(storeId);
        return productJpaRepository.findAll(dynamicProductFilter(storeId, productFilter));
    }

    Specification<Product> dynamicProductFilter(Long storeId, ProductFilter productFilter) {
        return (root, query, criteriaBuilder) -> {
            Set<Predicate> predicates = new HashSet<>();

            // Ensure storeId is valid and fetch store
            if (storeId == null) {
                throw new StoreNotFoundException("Store id is required");
            }
            Store store = storeService.getById(storeId);
            predicates.add(criteriaBuilder.equal(root.get("store"), store));

            // Filter by name with partial matching
            if (productFilter.getName() != null) {
                String pattern = "%" + productFilter.getName() + "%";
                predicates.add(criteriaBuilder.like(root.get("name"), pattern));
            }

            // Filter by quantity range
            if (productFilter.getLowerQuantityRange() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("quantity"), productFilter.getLowerQuantityRange()));
            }
            if (productFilter.getHigherQuantityRange() != null) {
                predicates.add(criteriaBuilder.lessThan(root.get("quantity"), productFilter.getHigherQuantityRange()));
            }

            // Filter by price range
            if (productFilter.getLowerPriceRange() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("sellingPrice"), productFilter.getLowerPriceRange()));
            }
            if (productFilter.getHigherPriceRange() != null) {
                predicates.add(criteriaBuilder.lessThan(root.get("sellingPrice"), productFilter.getHigherPriceRange()));
            }

            // Filter by brand
            if (productFilter.getBrandId() != null) {
                Brand brand = brandService.getBrandById(storeId, productFilter.getBrandId());
                predicates.add(criteriaBuilder.equal(root.get("brand"), brand));
            }

            // Filter by CGST/SGST rates
            if (productFilter.getCgstRate() != null) {
                predicates.add(criteriaBuilder.equal(root.get("cgst"), productFilter.getCgstRate()));
            }
            if (productFilter.getSgstRate() != null) {
                predicates.add(criteriaBuilder.equal(root.get("sgst"), productFilter.getSgstRate()));
            }

            // Filter by lastModifiedOn date range
            if (productFilter.getFromDate() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("lastModifiedOn"), productFilter.getFromDate()));
            }
            if (productFilter.getToDate() != null) {
                predicates.add(criteriaBuilder.lessThan(root.get("lastModifiedOn"), productFilter.getToDate()));
            }

            // Filter by categories (ensure all categories are matched)
            if (productFilter.getCategoryIds() != null && !productFilter.getCategoryIds().isEmpty()) {
                query.distinct(true); // Ensure distinct results since we're dealing with joins
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<Product> subqueryRoot = subquery.from(Product.class);
                Join<Object, Object> subqueryCategories = subqueryRoot.join("categories");

                // Group products and filter where category count matches
                subquery.select(subqueryRoot.get("id"))
                        .where(criteriaBuilder.and(
                                subqueryRoot.get("id").in(root.get("id")), // Match product
                                subqueryCategories.get("id").in(productFilter.getCategoryIds()) // Match categories
                        ))
                        .groupBy(subqueryRoot.get("id"))
                        .having(criteriaBuilder.equal(criteriaBuilder.count(subqueryCategories.get("id")), productFilter.getCategoryIds().size()));

                predicates.add(root.get("id").in(subquery));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
