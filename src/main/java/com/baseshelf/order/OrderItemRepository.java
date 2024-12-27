package com.baseshelf.order;

import com.baseshelf.brand.Brand;
import com.baseshelf.report.dto.OrderItemReport;
import com.baseshelf.store.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("SELECT MONTH(oi.createdOn) AS month, p.brand.id AS id, p.brand.name AS name, count(p.id) AS products, count(oi.productOrder) AS orderCount, sum(oi.quantity) AS quantity, sum(oi.amount) AS amount FROM OrderItem oi " +
            "INNER JOIN Product p " +
            "ON oi.product = p " +
            "WHERE p.brand IN :brands " +
            "AND p.store = :store " +
            "AND YEAR(oi.createdOn) = :year " +
            "AND MONTH(oi.createdOn) BETWEEN :lowerMonth AND :upperMonth " +
            "GROUP BY MONTH(oi.createdOn), id, name " +
            "ORDER BY MONTH(oi.createdOn) DESC ")
    List<Object[]> insightsOfBrandsByMonth(Store store, List<Brand> brands, Integer year, Integer lowerMonth, Integer upperMonth);

    @Query("SELECT oi.createdOn AS date, p.brand.id AS id, p.brand.name AS name, COUNT(DISTINCT p.id) AS products, COUNT(DISTINCT oi.productOrder) AS orders, SUM(oi.quantity) AS quantity, SUM(oi.amount) AS revenue FROM OrderItem oi " +
            "INNER JOIN Product p " +
            "ON oi.product = p " +
            "WHERE p.store = :store " +
            "AND p.brand IN :brands " +
            "AND oi.createdOn BETWEEN :from AND :to " +
            "GROUP BY oi.createdOn, id, name " +
            "ORDER BY oi.createdOn ASC ")
    List<Object[]> insightsOfBrandsByDateRange(Store store, List<Brand> brands, LocalDate from, LocalDate to);

    @Query(value = """
        SELECT * 
        FROM (
            SELECT 
                date_part('month', oi.created_on) AS month, 
                p.id AS product_id, 
                p.name AS name, 
                p.selling_price AS price,
                SUM(oi.quantity) AS total_quantity, 
                COUNT(DISTINCT oi.order_id) AS orders, 
                SUM(oi.amount) AS revenue, 
                b.id AS brand, 
                b.name AS brandName, 
                ROW_NUMBER() OVER (PARTITION BY date_part('month', oi.created_on) ORDER BY SUM(oi.quantity) DESC, SUM(oi.amount) DESC) AS rank
            FROM order_item oi
            INNER JOIN product p ON oi.product_id = p.id 
            INNER JOIN brand b ON p.brand_id = b.id 
            WHERE p.store_id = :storeId 
              AND date_part('year', oi.created_on) = :year 
              AND date_part('month', oi.created_on) BETWEEN :lowerMonth AND :upperMonth 
              AND p.id IN :productIds
            GROUP BY date_part('month', oi.created_on), p.id, brand, brandName
        ) ranked_products
        WHERE rank <= :limit
        ORDER BY month DESC, rank
    """, nativeQuery = true)
    List<Object[]> insightsOfProductsMonth(Long storeId, Integer year, Integer lowerMonth, Integer upperMonth, List<Long> productIds, Integer limit);

    @Query(value = """
        SELECT * 
        FROM (
            SELECT 
                date_part('month', oi.created_on) AS month, 
                p.id AS product_id, 
                p.name AS name, 
                p.selling_price AS price,
                SUM(oi.quantity) AS total_quantity, 
                COUNT(DISTINCT oi.order_id) AS orders, 
                SUM(oi.amount) AS revenue, 
                b.id AS brand, 
                b.name AS brandName, 
                ROW_NUMBER() OVER (PARTITION BY date_part('month', oi.created_on) ORDER BY SUM(oi.quantity) DESC, SUM(oi.amount) DESC) AS rank
            FROM order_item oi
            INNER JOIN product p ON oi.product_id = p.id 
            INNER JOIN brand b ON p.brand_id = b.id 
            WHERE p.store_id = :storeId 
              AND date_part('year', oi.created_on) = :year 
              AND date_part('month', oi.created_on) BETWEEN :lowerMonth AND :upperMonth 
            GROUP BY date_part('month', oi.created_on), p.id, brand, brandName
        ) ranked_products
        WHERE rank <= :limit
        ORDER BY month DESC, rank
    """, nativeQuery = true)
    List<Object[]> insightsOfProductsMonth(Long storeId, Integer year, Integer lowerMonth, Integer upperMonth, Integer limit);

    @Query(value = """
        SELECT * 
        FROM (
            SELECT 
                oi.created_on AS date, 
                p.id AS product_id, 
                p.name AS name, 
                p.selling_price AS price,
                SUM(oi.quantity) AS total_quantity, 
                COUNT(DISTINCT oi.order_id) AS orders, 
                SUM(oi.amount) AS revenue, 
                b.id AS brand, 
                b.name AS brandName, 
                ROW_NUMBER() OVER (PARTITION BY oi.created_on ORDER BY SUM(oi.quantity) DESC, SUM(oi.amount) DESC) AS rank
            FROM order_item oi
            INNER JOIN product p ON oi.product_id = p.id 
            INNER JOIN brand b ON p.brand_id = b.id 
            WHERE p.store_id = :storeId 
                AND oi.created_on BETWEEN :from AND :to 
                AND p.id IN :productIds
            GROUP BY oi.created_on, p.id, brand, brandName
        ) ranked_products
        WHERE rank <= :limit
        ORDER BY date DESC, rank
    """, nativeQuery = true)
    List<Object[]> insightsOfProductsDate(Long storeId, LocalDate from, LocalDate to, List<Long> productIds, Integer limit);

    @Query(value = """
        SELECT * 
        FROM (
            SELECT 
                oi.created_on AS date, 
                p.id AS product_id, 
                p.name AS name, 
                p.selling_price AS price,
                SUM(oi.quantity) AS total_quantity, 
                COUNT(DISTINCT oi.order_id) AS orders, 
                SUM(oi.amount) AS revenue, 
                b.id AS brand, 
                b.name AS brandName, 
                ROW_NUMBER() OVER (PARTITION BY oi.created_on ORDER BY SUM(oi.quantity) DESC, SUM(oi.amount) DESC) AS rank
            FROM order_item oi
            INNER JOIN product p ON oi.product_id = p.id 
            INNER JOIN brand b ON p.brand_id = b.id 
            WHERE p.store_id = :storeId 
                AND oi.created_on BETWEEN :from AND :to 
            GROUP BY oi.created_on, p.id, brand, brandName 
        ) ranked_products
        WHERE rank <= :limit
        ORDER BY date DESC, rank
    """, nativeQuery = true)
    List<Object[]> insightsOfProductsDate(Long storeId, LocalDate from, LocalDate to, Integer limit);

    @Query(value = """
        SELECT * 
        FROM ( 
            SELECT date_part('month', oi.created_on) AS month,  
            c.id AS id, 
            c.name AS name, 
            count(distinct p.id) AS productCount, 
            count(distinct po.id) AS orderCount, 
            sum(oi.quantity) AS quantity, 
            sum(oi.amount) AS revenue, 
            ROW_NUMBER() OVER (PARTITION BY date_part('month', oi.created_on) ORDER BY SUM(oi.quantity) DESC, SUM(oi.amount) DESC) AS rank 
            FROM order_item oi 
            INNER JOIN product p ON p.id = oi.product_id 
            INNER JOIN product_categories pc on pc.product_id = p.id 
            INNER JOIN category c ON c.id = pc.category_id 
            INNER JOIN product_order po ON oi.order_id = po.id 
            WHERE p.store_id = :storeId 
            AND date_part('year', oi.created_on) = :year  
            AND date_part('month', oi.created_on) BETWEEN :lowerMonth AND :upperMonth  
            GROUP BY c.id, c.name, date_part('month', oi.created_on) 
            ORDER BY quantity desc 
            ) ranked_products 
        WHERE rank <= :limit 
        ORDER BY month DESC, rank  
            """, nativeQuery = true)
    List<Object[]> insightsOfCategoryMonth(Long storeId, Integer year, Integer lowerMonth, Integer upperMonth, Integer limit);

    @Query(value = """
        SELECT * 
        FROM (
            SELECT date_part('month', oi.created_on) AS month, 
            c.id AS id, 
            c.name AS name, 
            count(distinct p.id) AS productCount, 
            count(distinct po.id) AS orderCount, 
            sum(oi.quantity) AS quantity, 
            sum(oi.amount) AS revenue, 
            ROW_NUMBER() OVER (PARTITION BY date_part('month', oi.created_on) ORDER BY SUM(oi.quantity) DESC, SUM(oi.amount) DESC) AS rank
            FROM order_item oi 
            INNER JOIN product p ON p.id = oi.product_id 
            INNER JOIN product_categories pc on pc.product_id = p.id 
            INNER JOIN category c ON c.id = pc.category_id 
            INNER JOIN product_order po ON oi.order_id = po.id 
            WHERE p.store_id = :storeId 
            AND date_part('year', oi.created_on) = :year  
            AND date_part('month', oi.created_on) BETWEEN :lowerMonth AND :upperMonth  
            AND c.id IN :categoryIds 
            GROUP BY c.id, c.name, date_part('month', oi.created_on) 
            ORDER BY quantity desc 
            ) ranked_products
        WHERE rank <= :limit
        ORDER BY month DESC, rank  
            """, nativeQuery = true)
    List<Object[]> insightsOfCategoryMonth(Long storeId, Integer year, Integer lowerMonth, Integer upperMonth, List<Long> categoryIds, Integer limit);

    @Query(value = """
        SELECT * 
        FROM (
            SELECT oi.created_on AS date, 
            c.id AS id, 
            c.name AS name, 
            count(distinct p.id) AS productCount, 
            count(distinct po.id) AS orderCount, 
            sum(oi.quantity) AS quantity, 
            sum(oi.amount) AS revenue, 
            ROW_NUMBER() OVER (PARTITION BY oi.created_on ORDER BY SUM(oi.quantity) DESC, SUM(oi.amount) DESC) AS rank
            FROM order_item oi 
            INNER JOIN product p ON p.id = oi.product_id 
            INNER JOIN product_categories pc on pc.product_id = p.id 
            INNER JOIN category c ON c.id = pc.category_id 
            INNER JOIN product_order po ON oi.order_id = po.id 
            WHERE p.store_id = :storeId 
            AND oi.created_on BETWEEN :from AND :to  
            GROUP BY c.id, c.name, oi.created_on 
            ORDER BY quantity desc 
            ) ranked_products
        WHERE rank <= :limit
        ORDER BY date DESC, rank  
            """, nativeQuery = true)
    List<Object[]> insightsOfCategoryDateRange(Long storeId, LocalDate from, LocalDate to, Integer limit);

    @Query(value = """
        SELECT * 
        FROM (
            SELECT oi.created_on AS date, 
            c.id AS id, 
            c.name AS name, 
            count(distinct p.id) AS productCount, 
            count(distinct po.id) AS orderCount, 
            sum(oi.quantity) AS quantity, 
            sum(oi.amount) AS revenue, 
            ROW_NUMBER() OVER (PARTITION BY oi.created_on ORDER BY SUM(oi.quantity) DESC, SUM(oi.amount) DESC) AS rank
            FROM order_item oi 
            INNER JOIN product p ON p.id = oi.product_id 
            INNER JOIN product_categories pc on pc.product_id = p.id 
            INNER JOIN category c ON c.id = pc.category_id 
            INNER JOIN product_order po ON oi.order_id = po.id 
            WHERE p.store_id = :storeId 
            AND oi.created_on BETWEEN :from AND :to  
            AND c.id IN :categoryIds 
            GROUP BY c.id, c.name, oi.created_on 
            ORDER BY quantity desc 
            ) ranked_products
        WHERE rank <= :limit
        ORDER BY date DESC, rank  
            """, nativeQuery = true)
    List<Object[]> insightsOfCategoryDateRange(Long storeId, LocalDate from, LocalDate to, List<Long> categoryIds, Integer limit);

    @Query(
            """
            SELECT new com.baseshelf.report.dto.OrderItemReport(
                oi.createdOn, 
                oi.id, 
                oi.product.id, 
                oi.quantity, 
                oi.productOrder.id, 
                oi.amount
            ) 
            FROM OrderItem oi 
            ORDER BY oi.createdOn DESC
            """
    )
    List<OrderItemReport> getAllOrderItems();
}
