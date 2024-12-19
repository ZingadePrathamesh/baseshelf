package com.baseshelf.order;

import com.baseshelf.product.Product;
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
import java.util.Map;
import java.util.Optional;

@Repository
public interface ProductOrderRepository extends JpaRepository<ProductOrder, Long>, JpaSpecificationExecutor<ProductOrder>{
    List<ProductOrderResponse> findAllByStore(Store store);
    Optional<ProductOrderResponse> findResponseByStoreAndId(Store store, Long id);
    Optional<ProductOrder> findByStoreAndId(Store store, Long orderId);

    @Transactional
    @Modifying
    void deleteByStoreAndId(Store store, Long orderId);

    @Query("SELECT po.createdOn, SUM(po.totalAmount) FROM ProductOrder po " +
            "WHERE po.store = :store " +
            "AND po.createdOn IN :dates " +
            "GROUP BY po.createdOn")
    List<Object[]> findRevenueByDateRange(@Param("store") Store store, @Param("dates") List<LocalDate> dates);

    @Query("SELECT MONTH(po.createdOn) AS month, SUM(po.totalAmount) AS totalRevenue " +
            "FROM ProductOrder po " +
            "WHERE po.store = :store " +
            "AND YEAR(po.createdOn) = :year " +
            "AND MONTH(po.createdOn) IN :months " +
            "GROUP BY MONTH(po.createdOn)")
    List<Object[]> findRevenueByMonthAndYear(@Param("store") Store store,
                                             @Param("year") Integer year,
                                             @Param("months") List<Integer> months);

}
