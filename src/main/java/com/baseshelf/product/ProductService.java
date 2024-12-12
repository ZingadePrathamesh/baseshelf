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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public Product getByIdAndStore(Long productId, Long storeId) {
        Store store =  storeService.getById(storeId);
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

    public List<Product> getAllProductsByStoreAndProductIds(Long storeId, List<Long> productIds){
        Store store = storeService.getById(storeId);
        return productIds.stream()
                .map((id)->this.getByIdAndStore(id, store))
                .toList();
    }

    public List<Product> getAllByStoreAndIds(Long storeId, Set<Long> productIds){
        Store store = storeService.getById(storeId);
        return productJpaRepository.findAllByStoreAndIdIn(store, productIds);
    }

    @Transactional
    public Set<Product> validateProductsAndQuantity(Long storeId, Map<Long, Integer> productMap){
        Store store =  storeService.getById(storeId);
        List<Product> uncheckProducts = productJpaRepository.findAllByStoreAndIdIn(store, productMap.keySet());
        Set<Product> checkedProducts = new HashSet<>();

        Map<Long, Product> productMap1 = uncheckProducts.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        for(Map.Entry<Long, Integer> pm : productMap.entrySet()){
            Product product = productMap1.get(pm.getKey());

            int newValue = product.getQuantity() - pm.getValue();

            if(newValue < 0){
               throw new ProductQuantityExceedException("Product with id: " + pm.getKey() + " exceeds in quantity by "+ Math.abs(value));
           }
           else{
               product.setQuantity(newValue);
               checkedProducts.add(product);
               break;
           }
        }

//        for(Map.Entry<Long, Integer> pm : productMap.entrySet()){
//            boolean flag = false;
//
//           for(Product product: uncheckProducts){
//               if(product.getId().equals(pm.getKey())){
//                   flag = true;
//                   int value = product.getQuantity() - pm.getValue();
//                   if(value < 0){
//                       throw new ProductQuantityExceedException("Product with id: " + pm.getKey() + " exceeds in quantity by "+ Math.abs(value));
//                   }
//                   else{
//                       product.setQuantity(value);
//                       checkedProducts.add(product);
//                       break;
//                   }
//               }
//           }
//
//           if(!flag){
//               throw new ProductNotFoundException("product with id: " + pm.getKey() + " does not exist");
//           }
//        }
        return checkedProducts;
    }

    public Product saveProduct(Long storeId, @Valid Product product) {
        Store store = storeService.getById(storeId);
        Brand brand = brandService.getBrandById(storeId, product.getBrand().getId());
        List<Long> categoryIds = product.getCategories()
                .stream()
                .map(Category::getId)
                .toList();
        Set<Category> categories = categoryService.getAllByStoreAndIdIn(storeId, categoryIds);

        product.setStore(store);
        product.setBrand(brand);
        product.setCategories(new ArrayList<>(categories)); // Convert Set to List

        // Save the product and generate the barcode
        Product savedProduct = productJpaRepository.save(product);

        // Save the updated product with the generated barcode
        return productJpaRepository.save(savedProduct);
    }

    @Transactional
    public Product updateProductByStoreAndId(Long storeId, Long productId, @Valid Product product) {
        Brand brand = brandService.getBrandById(storeId, product.getBrand().getId());
        List<Long> categoryIds = product.getCategories().stream()
                .map(Category::getId)
                .toList();
        Set<Category> categories = categoryService.getAllByStoreAndIdIn(storeId, categoryIds);

        Product oldProduct = this.getByIdAndStore(productId, storeId);
        oldProduct.setName(product.getName());
        oldProduct.setDescription(product.getDescription());
        oldProduct.setSellingPrice(product.getSellingPrice());
        oldProduct.setCostPrice(product.getCostPrice());
        oldProduct.setTaxed(product.isTaxed());
        oldProduct.setCgst(product.getCgst());
        oldProduct.setSgst(product.getSgst());
        oldProduct.setQuantity(product.getQuantity());
        oldProduct.setCategories(categories.stream().toList());
        oldProduct.setBrand(brand);
        oldProduct.setActive(product.isActive());
        oldProduct.setLastModifiedOn(LocalDate.now());
        return oldProduct;
    }

    public void deleteById(Long storeId, List<Long> productIds){
        Store store =  storeService.getById(storeId);
        productJpaRepository.deleteByStoreAndIdIn(store, productIds);
    }

    @Transactional
    public Product sellProductByProductId(Long storeId, Long productId, Integer quantity){
        Product product = getByIdAndStore(productId, storeId);
        int newQuantity = product.getQuantity() - quantity;
        if(newQuantity > 0){
            product.setQuantity(newQuantity);
            return product;
        }
        else{
           throw new ProductQuantityExceedException("Provided quantity exceeds the available quantity by: " + Math.abs(newQuantity));
        }
    }

    @Transactional
    public Product restockProduct(Long storeId, Long productId, Integer quantity){
        Product product = getByIdAndStore(productId, storeId);
        product.setQuantity(product.getQuantity() + quantity);
        return product;
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
