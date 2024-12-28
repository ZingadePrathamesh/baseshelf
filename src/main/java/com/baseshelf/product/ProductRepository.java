package com.baseshelf.product;

import com.baseshelf.brand.Brand;
import com.baseshelf.store.Store;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    @Query("SELECT p FROM Product p WHERE p.store = :store " +
            " AND NOT EXISTS " +
            "(SELECT c FROM p.categories c WHERE c.categoryType = :categoryType)")
    List<Product> findAllWithoutCategoryType(Store store, @Param("categoryType") String categoryType);

    Optional<Product> findByIdAndStore(Long productId, Store store);

    List<Product> findAllByStore(Store store);

    List<Product> findAllByStoreAndBrand(Store store, Brand brand);

    @Query("SELECT p FROM Product p " +
            "JOIN p.categories c " +
            "WHERE p.store = :store " +
            "AND c.id IN :categoryIds " +
            "GROUP BY p.id " +
            "HAVING COUNT(c.id) = :categoryCount")
    List<Product> findByAllCategoryIdsAndStore(@Param("store") Store store,
                                               @Param("categoryIds") List<Long> categoryIds,
                                               @Param("categoryCount") long categoryCount);

    @Transactional
    @Modifying
    void deleteByStoreAndIdIn(Store store, List<Long> productIds);
    List<Product> findAllByStoreAndIdIn(Store store, Set<Long> productIds);
}
