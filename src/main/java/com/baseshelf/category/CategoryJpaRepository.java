package com.baseshelf.category;

import com.baseshelf.store.Store;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryJpaRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {
    List<Category> findAllByStoreOrGlobal(Store store, boolean b);
    List<Category> findAllByGlobal(boolean global);
    Optional<Category> findByIdAndStore(Long categoryId, Store store);
    Optional<Category> findByStoreAndNameAndCategoryType(Store store, @Size(message = "Name should be between 2 to 30 characters", max = 30, min = 2) @NotNull(message = "Name cannot be null.") String name, @Size(message = "Category Type should be between 2 to 30 characters", max = 30, min = 2) @NotNull(message = "Category Type cannot be null.") String categoryType);
    List<Category> findAllByStore(Store store);
}
