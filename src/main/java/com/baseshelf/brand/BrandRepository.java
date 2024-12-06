package com.baseshelf.brand;

import com.baseshelf.product.Product;
import com.baseshelf.store.Store;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    List<Brand> findAllByStore(Store store);
    Optional<Brand> findByStoreAndId(Store store, Long brandId);
    Long countByStore(Store store);
    Optional<Brand> findByStoreAndName(Store store, @Size(message = "Name should be between 2 to 30 characters", max = 30, min = 2) @NotNull(message = "Name cannot be null.") String name);
    List<Brand> findAllByStoreOrderByProductsDesc(Store store);
}
