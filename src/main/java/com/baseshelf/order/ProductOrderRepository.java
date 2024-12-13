package com.baseshelf.order;

import com.baseshelf.store.Store;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductOrderRepository extends JpaRepository<ProductOrder, Long>, JpaSpecificationExecutor<ProductOrder>{
    List<ProductOrderResponse> findAllByStore(Store store);
    Optional<ProductOrderResponse> findResponseByStoreAndId(Store store, Long id);
    Optional<ProductOrder> findByStoreAndId(Store store, Long orderId);

    @Transactional
    @Modifying
    void deleteByStoreAndId(Store store, Long orderId);
}
