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

    @Query("SELECT p.brand.id as brandId, MONTH(oi.createdOn) AS month, count(p.id) AS products, count(oi.productOrder) AS orderCount, sum(oi.quantity) AS quantity, sum(oi.amount) AS amount FROM OrderItem oi " +
            "INNER JOIN Product p " +
            "ON oi.product = p " +
            "WHERE p.brand = :brand " +
            "AND MONTH(oi.createdOn) IN :months " +
            "AND p.store = :store " +
            "AND YEAR(oi.createdOn) = :year " +
            "GROUP BY MONTH(oi.createdOn), p.brand")
    List<Object[]> analysisByBrandByYearAndMonth(@Param("store") Store store, @Param("brand") Brand brand, @Param("year") Integer year, @Param("months") List<Integer> months);

    @Query("SELECT oi.createdOn AS date, SUM(oi.quantity), COUNT(p.id) AS products, SUM(amount) AS revenue FROM OrderItem oi\n" +
            "INNER JOIN Product p " +
            "ON oi.product = p " +
            "WHERE p.brand = :brand " +
            "AND p.store = :store " +
            "AND oi.createdOn BETWEEN :from AND :to " +
            "GROUP BY oi.createdOn")
    List<Object[]> analysisOfBrandByDateRange(Store store, Brand brand, LocalDate from, LocalDate to);

    @Query("SELECT p.brand.id as brandId, oi.createdOn AS date, SUM(oi.quantity), COUNT(p.id) AS products, SUM(amount) AS revenue FROM OrderItem oi " +
            "INNER JOIN Product p " +
            "ON oi.product = p " +
            "WHERE p.brand IN :brands " +
            "AND p.store = :store " +
            "AND oi.createdOn BETWEEN :from AND :to " +
            "GROUP BY oi.createdOn, p.brand")
    List<Object[]> analysisOfMultipleBrandsByDateRange(Store store, List<Brand> brands, LocalDate from, LocalDate to);

    @Query("SELECT p.brand.id as brandId, MONTH(oi.createdOn) AS month, count(p.id) AS products, count(oi.productOrder) AS orderCount, sum(oi.quantity) AS quantity, sum(oi.amount) AS amount FROM OrderItem oi " +
            "INNER JOIN Product p " +
            "ON oi.product = p " +
            "WHERE p.brand IN :brands " +
            "AND p.store = :store " +
            "AND MONTH(oi.createdOn) IN :months " +
            "AND YEAR(oi.createdOn) = :year " +
            "GROUP BY MONTH(oi.createdOn), p.brand")
    List<Object[]> analysisOfMultipleBrandsByMonthYear(Store store, List<Brand> brands, Integer year, List<Integer> months);
}
