package com.baseshelf.product;

import com.baseshelf.brand.Brand;
import com.baseshelf.category.Category;
import com.baseshelf.store.Store;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    @Query("SELECT p FROM Product p WHERE NOT EXISTS " +
            "(SELECT c FROM p.categories c WHERE c.categoryType = :categoryType)")
    List<Product> findAllWithoutCategoryType(@Param("categoryType") String categoryType);

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

    @Query("SELECT oi.createdOn AS date, p.id AS productId, p.sellingPrice AS sellingPrice, SUM(oi.quantity) AS quantity, SUM(oi.amount) AS revenue, p.brand AS brandId FROM OrderItem oi " +
            "INNER JOIN Product p ON oi.product = p " +
            "WHERE p.store = :store " +
            "AND oi.createdOn BETWEEN :from AND :to " +
            "GROUP BY oi.createdOn, p.id, p.brand " +
            "ORDER BY oi.createdOn DESC, SUM(oi.quantity) DESC")
    List<Object[]> findProductDataByDateRange(Store store, LocalDate from, LocalDate to);

    @Query(value = """
        SELECT * 
        FROM (
            SELECT 
                oi.created_on, 
                p.id AS product_id, 
                p.selling_price AS price,
                SUM(oi.quantity) AS total_quantity,
                SUM(oi.amount) AS revenue,
                p.brand_id AS brand,
                ROW_NUMBER() OVER (PARTITION BY oi.created_on ORDER BY SUM(oi.quantity) DESC) AS rank
            FROM order_item oi
            INNER JOIN product p ON oi.product_id = p.id
            WHERE oi.created_on BETWEEN :from AND :to
              AND p.store_id = :storeId
            GROUP BY oi.created_on, p.id, p.brand_id
        ) ranked_products
        WHERE rank <= :limit
        ORDER BY created_on DESC, rank
    """, nativeQuery = true)
    List<Object[]> findTopProductsByDateRange(
            @Param("storeId") Long storeId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("limit") int limit
    );

    @Query("SELECT MONTH(oi.createdOn) AS month, p.id AS product, p.sellingPrice AS price, SUM(oi.quantity) AS quantity, SUM(oi.amount) AS amount, p.brand AS brand FROM OrderItem oi " +
            "INNER JOIN Product p " +
            "ON oi.product = p " +
            "WHERE p.store = :store " +
            "AND YEAR(oi.createdOn) = :year " +
            "AND MONTH(oi.createdOn) IN :months " +
            "GROUP BY MONTH(oi.createdOn), p.id, p.brand " +
            "ORDER BY MONTH(oi.createdOn) DESC, SUM(oi.quantity) DESC")
    List<Object[]> findProductsDataByMonth(Store store, Integer year, List<Integer> months);

    @Query(value = """
        SELECT * 
        FROM (
            SELECT 
                date_part('month', oi.created_on) AS month, 
                p.id AS product_id, 
                p.selling_price AS price,
                SUM(oi.quantity) AS total_quantity,
                SUM(oi.amount) AS revenue,
                p.brand_id AS brand,
                ROW_NUMBER() OVER (PARTITION BY date_part('month', oi.created_on) ORDER BY SUM(oi.quantity) DESC) AS rank
            FROM order_item oi
            INNER JOIN product p ON oi.product_id = p.id
            WHERE date_part('month', oi.created_on) IN :months
              AND p.store_id = :storeId
              AND date_part('year', oi.created_on) = :year
            GROUP BY date_part('month', oi.created_on), p.id, p.brand_id
        ) ranked_products
        WHERE rank <= :limit
        ORDER BY month DESC, rank
    """, nativeQuery = true)
    List<Object[]> findTopProductsDataByMonth(Long storeId, Integer year, List<Integer> months, Integer limit);
}
