package com.baseshelf.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryJpaRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {
    Optional<Category> findByNameAndCategoryType(String name, String categoryType);
    List<Category> findAllByCategoryType(String paramCategoryType);
    List<Category> findByName(String name);
}
