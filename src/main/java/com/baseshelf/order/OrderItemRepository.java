package com.baseshelf.order;

import com.baseshelf.brand.Brand;
import com.baseshelf.store.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
}
