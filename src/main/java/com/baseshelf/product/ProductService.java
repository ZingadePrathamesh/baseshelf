package com.baseshelf.product;

import com.baseshelf.category.Category;
import com.baseshelf.category.CategoryService;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductJpaRepository productJpaRepository;

    @Bean
    @Order(value = 3)
    public CommandLineRunner insertProduct(
            ProductJpaRepository productJpaRepository,
            CategoryService categoryService
    ){
        return args -> {
            Faker faker = new Faker();
            Set<Product> products = new HashSet<>();
            List<Category> categories = categoryService.getAllByIdOrNameOrCategoryType(null, null,"COLOR");
            for (int i = 0; i<100; i++){
                List<Category> categorySet = new ArrayList<>();
                categorySet.add(categories.get(faker.number().numberBetween(0, categories.size())));
                Product product = Product.builder()
                        .name("product"+ i)
                        .description("Something about product"+ i)
                        .categories(categorySet)
                        .build();
                products.add(product);
            }
            productJpaRepository.saveAll(products);
            System.out.println("All products have been added");
            deleteProductById(1L);
        };
    }

    public void deleteProductById(Long id){
        productJpaRepository.deleteById(id);
    }
}
