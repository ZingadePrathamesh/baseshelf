package com.baseshelf.category;

import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

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
                        .category_type("COLOR".toUpperCase())
                        .build();
                categories.add(category);
            }
            categoryJpaRepository.saveAll(categories);
        };
    }
}
