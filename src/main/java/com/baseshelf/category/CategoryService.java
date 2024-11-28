package com.baseshelf.category;

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
public class CategoryService {
    private final CategoryJpaRepository categoryJpaRepository;

    @Bean
    @Order(value = 1)
    public CommandLineRunner commandLineRunner(
            CategoryJpaRepository categoryJpaRepository
    ){
        return args -> {
            Faker faker = new Faker();
            Set<Category> categories = new HashSet<>();
            for (int i = 0; i< 50; i++){
                String color = faker.color().name();
                Category category = Category.builder()
                        .name(color)
                        .categoryType("COLOR".toUpperCase())
                        .build();
                categories.add(category);
            }
            categoryJpaRepository.saveAll(categories);
        };
    }

//    @Bean
    @Order(value = 2)
    public CommandLineRunner testingMethods(
            CategoryJpaRepository categoryJpaRepository
    ){
        return args -> {
            saveCategory(Category.builder()
                    .name("blck")
                    .categoryType("COLOR")
//                    .products(null)
                    .build());

//            updateById(this.getAllByIdOrNameOrCategoryType(null, "blck", "COLOR")
//                            .getFirst().getId(),
//                    Category.builder().name("black").categoryType("COLOR").build());
//
//            deleteByIdOrNameOrCategoryType(null, "YELLOW", "COLOR");
//            Long id  = 1L;

//            System.out.println(getAllByIdOrNameOrCategoryType(id, null, null));
        };
    }

    //Saves a category if not already present. Otherwise returns the old.
    public Category saveCategory(Category paramCategory){
        Optional<Category> categoryOpt = categoryJpaRepository.findByNameAndCategoryType
                (paramCategory.getName(), paramCategory.getCategoryType());
        return categoryOpt.orElseGet(() -> categoryJpaRepository.save(paramCategory));
    }

    public List<Category> getAllByIdOrNameOrCategoryType(Long id, String paramName, String paramCategoryType){
        List<Category> categories = categoryJpaRepository.findAll(dynamicFilterByParams(id, paramName, paramCategoryType));
        return categories;
    }

    public void deleteByIdOrNameOrCategoryType(Long id, String name, String categoryType){
        categoryJpaRepository.delete(dynamicFilterByParams(id, name, categoryType));
    }

    public Category updateById(Long id, Category paramCategory){
        this.deleteByIdOrNameOrCategoryType(id, null, null);
        paramCategory.setId(id);
        return this.saveCategory(paramCategory);
    }

    //Used specifications to filter according to the availability of the conditions (id, name, type)
    Specification<Category> dynamicFilterByParams(Long id, String name, String categoryType){
        return (root, query, criteriaBuilder) -> {
          List<Predicate> predicates = new ArrayList<>();
          if(id != null)
              predicates.add(criteriaBuilder.equal(root.get("id"), id));
          if(name != null)
              predicates.add(criteriaBuilder.equal(root.get("name"), name));
          if(categoryType != null)
              predicates.add(criteriaBuilder.equal(root.get("categoryType"), categoryType));

          return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
