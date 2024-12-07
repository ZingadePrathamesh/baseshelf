package com.baseshelf.product;

import com.baseshelf.brand.Brand;
import com.baseshelf.category.Category;
import com.baseshelf.store.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductJpaRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    @Query("SELECT p FROM Product p WHERE NOT EXISTS " +
            "(SELECT c FROM p.categories c WHERE c.categoryType = :categoryType)")
    List<Product> findAllWithoutCategoryType(@Param("categoryType") String categoryType);

    Optional<Product> findByIdAndStore(Long productId, Store store);

    List<Product> findAllByStore(Store store);

    List<Product> findAllByStoreAndBrand(Store store, Brand brand);

    @Query("SELECT p FROM Product p " +
            "JOIN p.categories c " +
            "WHERE c.id IN :categoryIds " +
            "GROUP BY p.id " +
            "HAVING COUNT(c.id) = :categoryCount")
    List<Product> findByAllCategoryIds(@Param("categoryIds") List<Long> categoryIds,
                                       @Param("categoryCount") long categoryCount);

}
