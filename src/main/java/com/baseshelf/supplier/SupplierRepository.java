package com.baseshelf.supplier;

import com.baseshelf.store.Store;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    List<Supplier> findAllByStore(Store store);
    Optional<Supplier> findByStoreAndId(Store store, Long id);
    Long countByStore(Store store);

    Optional<Supplier> findByStoreAndName(Store store, @Size(message = "Name should be between 2 to 30 characters", max = 30, min = 2) @NotNull(message = "Name cannot be null.") String name);
}

