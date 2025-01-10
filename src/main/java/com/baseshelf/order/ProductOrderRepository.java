package com.baseshelf.order;

import com.baseshelf.analytics.dto.InventoryWorthInsight;
import com.baseshelf.store.Store;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.io.InvalidClassException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductOrderRepository extends JpaRepository<ProductOrder, Long>, JpaSpecificationExecutor<ProductOrder>{
//    List<ProductOrderResponse> findAllByStore(Store store);
    Optional<ProductOrder> findByStoreAndId(Store store, Long orderId);

    @Transactional
    @Modifying
    void deleteByStoreAndId(Store store, Long orderId);

    @Query("SELECT po.createdOn, SUM(po.totalAmount), SUM(po.itemCount) FROM ProductOrder po " +
            "WHERE po.store = :store " +
            "AND po.createdOn BETWEEN :from AND :to " +
            "GROUP BY po.createdOn")
    List<Object[]> findOrdersAnalysisByDateRange(@Param("store") Store store, @Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("SELECT MONTH(po.createdOn) AS month, SUM(po.itemCount), SUM(po.totalAmount) AS totalRevenue " +
            "FROM ProductOrder po " +
            "WHERE po.store = :store " +
            "AND YEAR(po.createdOn) = :year " +
            "AND MONTH(po.createdOn) IN :months " +
            "GROUP BY MONTH(po.createdOn)")
    List<Object[]> findRevenueByMonthAndYear(@Param("store") Store store,
                                             @Param("year") Integer year,
                                             @Param("months") List<Integer> months);


    @Query(value = """
            SELECT new com.baseshelf.analytics.dto.InventoryWorthInsight(
                p.id as ID, 
                p.quantity AS quantity,  
                p.costPrice AS costPrice,  
                p.sellingPrice AS sellingPrice, 
                p.quantity * p.costPrice AS investedAmount  
            )
            FROM Product p 
            WHERE p.store = :store 
            AND p.quantity > 0
            GROUP BY p.id
            ORDER BY investedAmount DESC
            """)
    List<InventoryWorthInsight> currentInvestedAmount(Store store);


    @Query(value = """
            SELECT new com.baseshelf.order.ProductOrderDto( 
            po.id AS id, 
            po.name AS name, 
            po.createdOn AS date, 
            po.orderTime AS time,
            po.itemCount AS count, 
            po.totalAmount AS total_amount, 
            po.totalGst AS gst 
            )
            FROM ProductOrder po 
            WHERE po.store = :store 
            AND po.createdOn BETWEEN :from AND :to 
            ORDER BY po.createdOn DESC 
            LIMIT :limit
            """)
    List<ProductOrderDto> findAllProductOrderByDateRange(Store store, LocalDate from, LocalDate to, Integer limit);
}
