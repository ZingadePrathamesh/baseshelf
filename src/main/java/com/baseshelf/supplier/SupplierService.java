package com.baseshelf.supplier;

import com.baseshelf.product.Product;
import com.baseshelf.product.ProductService;
import com.baseshelf.store.Store;
import com.baseshelf.store.StoreService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SupplierService {
    private final SupplierRepository supplierRepository;
    private final StoreService storeService;
    private final ProductService productService;

    @Bean
    @Order(value = 5)
    public CommandLineRunner commandLineRunner(
            StoreService storeService, SupplierRepository supplierRepository
    ){
        return args -> {
            Store store = storeService.getByEmail("johndoe@gmail.com");
            Faker faker= new Faker();
            Set<Supplier> suppliers = new HashSet<>();

            for(int i = 0; i< 10; i++){
                Supplier supplier = Supplier.builder()
                        .description("fake data")
                        .store(store)
                        .name((String) faker.name().title().subSequence(0, 15))
                        .gstin("abcdefghijk12345")
                        .build();
                suppliers.add(supplier);
            }
            supplierRepository.saveAll(suppliers);
        };
    }

    public List<Supplier> getAllSuppliersByStore(Long storeId) {
        Store store = storeService.getById(storeId);
        return supplierRepository.findAllByStore(store);
    }

    public Supplier getSupplierById(Long storeId, Long supplierId) {
        Store store = storeService.getById(storeId);
        return supplierRepository.findByStoreAndId(store, supplierId)
                .orElseThrow(() -> new SupplierNotFoundException("Supplier with id: " + supplierId + " does not exist!"));
    }

    public Supplier createSupplier(Long storeId, Supplier supplier) {
        Store store = storeService.getById(storeId);
        Optional<Supplier> supplierOptional = supplierRepository.findByStoreAndName(store, supplier.getName());
        supplier.setStore(store);
        return supplierOptional.orElseGet(() -> supplierRepository.save(supplier));
    }

    @Transactional
    public Supplier updateSupplier(Long storeId, Long supplierId, Supplier supplier) {
        Store store = storeService.getById(storeId);
        Supplier existingSupplier = supplierRepository.findByStoreAndId(store, supplierId)
                .orElseThrow(() -> new SupplierNotFoundException("Supplier with id: " + supplierId + " does not exist!"));
        existingSupplier.setName(supplier.getName());
        existingSupplier.setDescription(supplier.getDescription());
        return existingSupplier;
    }

    public void deleteSupplier(Long storeId, Long supplierId) {
        Store store = storeService.getById(storeId);
        Supplier supplier = supplierRepository.findByStoreAndId(store, supplierId)
                .orElseThrow(() -> new SupplierNotFoundException("Supplier with id: " + supplierId + " does not exist!"));
        supplierRepository.delete(supplier);
    }

    public List<Product> getProductsBySupplier(Long storeId, Long supplierId) {
        Supplier supplier = getSupplierById(storeId, supplierId);
        return supplier.getProducts();
    }

    public Long getCountByStoreId(Long storeId) {
        Store store = storeService.getById(storeId);
        return supplierRepository.countByStore(store);
    }
}


