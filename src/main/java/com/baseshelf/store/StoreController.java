package com.baseshelf.store;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("stores")
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;

    @GetMapping("")
    public List<Store> getAllStore(){
        return storeService.getAllStores();
    }

    @GetMapping("id/{id}")
    public Store getStoreById(@PathVariable(name = "id") Long id){
        return storeService.getById(id);
    }

    @GetMapping("count")
    public Long getStoreCount(){
        return storeService.getStoreCounts();
    }

    @PostMapping("/registers")
    public Store registerStore(@Valid @RequestBody Store store) throws MethodArgumentNotValidException, NoSuchMethodException {
        return storeService.registerStore(store);
    }

    @PutMapping("/disable/id/{id}")
    public void disableStore(@PathVariable(name = "id") Long id){
        storeService.disableStore(id);
    }

    @DeleteMapping("/delete/id/{id}")
    public void deleteStore(@PathVariable(name = "id") Long id){
        storeService.deleteStore(id);
    }

    @PutMapping("/update/id/{id}")
    public Store updateStore(@PathVariable(name = "id") Long id, @Valid @RequestBody StoreDto storeDto){
        return storeService.updateStoreById(id, storeDto);
    }
}
