package com.baseshelf.store;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
    public Store registerStore(@Valid @RequestBody Store store){
        return storeService.registerStore(store);
    }

}
