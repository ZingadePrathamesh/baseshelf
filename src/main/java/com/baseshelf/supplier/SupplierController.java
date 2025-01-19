package com.baseshelf.supplier;

import com.baseshelf.product.Product;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("stores/store-id/{store-id}/suppliers")
public class SupplierController {
    private final SupplierService supplierService;

    @GetMapping("")
    public List<Supplier> getAllSuppliers(@PathVariable("store-id") Long storeId) {
        return supplierService.getAllSuppliersByStore(storeId);
    }

    @GetMapping("/supplier-id/{supplier-id}")
    public Supplier getSupplierById(@PathVariable("store-id") Long storeId,
                                    @PathVariable("supplier-id") Long supplierId) {
        return supplierService.getSupplierById(storeId, supplierId);
    }

    @PostMapping("")
    public Supplier addSupplier(@PathVariable("store-id") Long storeId,
                                @RequestBody @Valid Supplier supplier) {
        return supplierService.createSupplier(storeId, supplier);
    }

    @PutMapping("/supplier-id/{supplier-id}")
    public Supplier updateSupplier(@PathVariable("store-id") Long storeId,
                                   @PathVariable("supplier-id") Long supplierId,
                                   @RequestBody @Valid Supplier supplier) {
        return supplierService.updateSupplier(storeId, supplierId, supplier);
    }

    @DeleteMapping("/supplier-id/{supplier-id}")
    public void deleteSupplier(@PathVariable("store-id") Long storeId,
                               @PathVariable("supplier-id") Long supplierId) {
        supplierService.deleteSupplier(storeId, supplierId);
    }

    @GetMapping("/supplier-id/{supplier-id}/products")
    public List<Product> getProductsBySupplier(@PathVariable("store-id") Long storeId,
                                               @PathVariable("supplier-id") Long supplierId) {
        return supplierService.getProductsBySupplier(storeId, supplierId);
    }

    @GetMapping("/counts")
    public Long getCountOfSupplier(@PathVariable("store-id") Long storeId) {
        return supplierService.getCountByStoreId(storeId);
    }
}

