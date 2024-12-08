package com.baseshelf.category;

import com.baseshelf.product.Product;
import com.baseshelf.product.ProductService;
import com.baseshelf.store.Store;
import com.baseshelf.store.StoreService;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import java.util.*;

@Service
public class CategoryService {
    private final CategoryJpaRepository categoryJpaRepository;
    private final StoreService storeService;
    private final ProductService productService;

    public CategoryService(CategoryJpaRepository categoryJpaRepository, StoreService storeService,@Lazy ProductService productService) {
        this.categoryJpaRepository = categoryJpaRepository;
        this.storeService = storeService;
        this.productService = productService;
    }

    @Bean
    @Order(value = 3)
    public CommandLineRunner insertCategory(
            CategoryJpaRepository categoryJpaRepository
    ){
        return args -> {
          Store store = storeService.getByEmail("johndoe@gmail.com");
          Store store1 = storeService.getByEmail("smartit@gmail.com");
          Faker faker = new Faker();
          Set<Category> categories = new HashSet<>();

          //inserting colors
          for(int i = 0; i< 100;i ++){
              Category category = Category.builder()
                      .global(true)
                      .store(store)
                      .categoryType("COLOR")
                      .name(faker.color().name())
                      .build();
              categories.add(category);
          }

          //inserting materials
          List<String> materials = List.of("Cotton", "Silk", "Linen", "Wool", "Polyester", "Denim", "Rug");
          for(String m: materials){
              Category category = Category.builder()
                      .global(true)
                      .store(store)
                      .categoryType("MATERIAL")
                      .name(m)
                      .build();
              categories.add(category);
          }

          //inserting product type
          List<String> productTypes = List.of("Shirt", "Tshirt", "Jeans", "Pants", "Trousers", "Cargo", "Tracks", "Paijama");
            for(String pT: productTypes){
                Category category = Category.builder()
                        .global(true)
                        .store(store)
                        .categoryType("PRODUCT TYPE")
                        .name(pT)
                        .build();
                categories.add(category);
            }

            //inserting sizes
            List<String> sizes = List.of("XXS", "XS", "S", "M", "L", "XL", "XXL");
            for(String s: sizes){
                Category category = Category.builder()
                        .global(true)
                        .store(store)
                        .categoryType("SIZE")
                        .name(s)
                        .build();
                categories.add(category);
            }

          categoryJpaRepository.saveAll(categories);
        };
    }

    //Saves a category if not already present. Otherwise returns the old.
    public Category saveCategory(Long storeId, Category paramCategory){
        Store store = storeService.getById(storeId);
        Optional<Category> categoryOpt = categoryJpaRepository.findByStoreAndNameAndCategoryType
                (store, paramCategory.getName(), paramCategory.getCategoryType());
        paramCategory.setStore(store);
        paramCategory.setGlobal(false);
        return categoryOpt.orElseGet(() -> categoryJpaRepository.save(paramCategory));
    }

    public List<Category> getAllByIdOrNameOrCategoryType(Long storeId, Long categoryId, String paramName, String paramCategoryType){
        return categoryJpaRepository.findAll(dynamicFilterByParams(storeId, categoryId, paramName, paramCategoryType));
    }

    public List<Category> getAllCategories() {
        return categoryJpaRepository.findAll();
    }

    public List<Category> getAllGlobalCategories(){
        return categoryJpaRepository.findAllByGlobal(true);
    }

    public Set<Category> getAllByStoreAndIdIn(Long storeId, List<Long> ids){
        Set<Category> categories = new HashSet<>();
        for(Long id: ids){
            categories.add(this.getByCategoryId(storeId, id));
        }
        return categories;
    }

    public List<Category> getAllByStoreIdAndGlobal(Long storeId, boolean global) {
        Store store = storeService.getById(storeId);
        if(global){
            return categoryJpaRepository.findAllByStoreOrGlobal(store, true);
        }
        else return categoryJpaRepository.findAllByStore(store);
    }

    public Category getByCategoryId(Long storeId, Long categoryId){
        Store store = storeService.getById(storeId);
        Optional<Category> categoryOptional = categoryJpaRepository.findByIdAndStore(categoryId, store);
        return categoryOptional.orElseThrow(()->
                new CategoryNotFoundException("Category with id: " + categoryId + " does not exist!")
        );
    }

    public List<Category> getAllCategoriesByNameOrCategoryType(Long storeId, String name, String categoryType) {
        return categoryJpaRepository.findAll(dynamicFilterByParams(storeId, null, name, categoryType));
    }

    public List<Category> getAllCategoriesByProduct(Long storeId, Long productId){
        Store store = storeService.getById(storeId);
        Product product = productService.getByIdAndStore(productId, store);
        return product.getCategories();
    }

    public void deleteByIdOrNameOrCategoryType(Long storeId, Long categoryId, String name, String categoryType){
        categoryJpaRepository.delete(dynamicFilterByParams(storeId, categoryId, name, categoryType));
    }

//    //refer to the pitfall no.3 of the article "6-performance-pitfalls-when-using-spring-data-jpa"
//    //In most cases, using @Transactional is better than relying on saveAndFlush for updates in Spring Data JPA.
//    //Caution while using this method, as it can cause unwanted effects bcz each category is mapped with many products.
//    //So change in the category value can cause issue with the data analysis.
    @Transactional
    public Category updateCategory(Long storeId, Long id, Category paramCategory){
        Store store = storeService.getById(storeId);

        Category category = categoryJpaRepository.findByIdAndStore(id, store)
                .orElseThrow(() -> new CategoryNotFoundException("Category with id: " + id + " does not exist."));

        //if any category exists with the same properties as passed in the new category the update will not occur.
        Optional<Category> existCategory = categoryJpaRepository.findByStoreAndNameAndCategoryType(store, paramCategory.getName(), paramCategory.getCategoryType());
        if(existCategory.isPresent()){
            throw new CategoryAlreadyExist("Category with name:" + paramCategory.getName() +
                    " and categoryType: "+ paramCategory.getCategoryType() + " already exists with id: " + existCategory.get().getId()
                    );
        }
        category.setCategoryType(paramCategory.getCategoryType());
        category.setName(paramCategory.getName());
        return category;
    }

    //Used specifications to filter according to the availability of the conditions (id, name, type)
    Specification<Category> dynamicFilterByParams(Long storeId, Long categoryId, String name, String categoryType){
        return (root, query, criteriaBuilder) -> {
          List<Predicate> predicates = new ArrayList<>();
          if(storeId != null){
              Store store = storeService.getById(storeId);
              predicates.add(criteriaBuilder.equal(root.get("store"), store));
          }
          if(categoryId != null)
              predicates.add(criteriaBuilder.equal(root.get("id"), categoryId));
          if(name != null)
              predicates.add(criteriaBuilder.equal(root.get("name"), name));
          if(categoryType != null)
              predicates.add(criteriaBuilder.equal(root.get("categoryType"), categoryType));

          return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
