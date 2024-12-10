package com.baseshelf.store;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;

    public Store getById(Long id){
        Optional<Store> storeOpt = storeRepository.findById(id);
        return storeOpt.orElseThrow(()-> new StoreNotFoundException("Store with id: "+ id+ " does not exist!"));
    }

    public List<Store> getAllStores(){
        return storeRepository.findAll();
    }

    public Long getStoreCounts(){
        return storeRepository.count();
    }

    public Store registerStore(Store store) throws MethodArgumentNotValidException, NoSuchMethodException {
            //some password logic to be integrated later
        Optional<Store> storeOpt = storeRepository.getStoreByEmail(store.getEmail());
        if(storeOpt.isPresent()){
            handleEmailAlreadyExistException(store);
        }
        return storeRepository.save(store);
    }


    @Bean
    @Order(value = 1)
    public CommandLineRunner insertStores(
            StoreRepository storeRepository
    ){
        return args -> {
            Store store = Store.builder()
                    .email("johndoe@gmail.com")
                    .password("strongPassword")
                    .description("Store for testing")
                    .name("John Doe Clothing")
                    .address("California")
                    .gstinNumber("1234567890ABCDE")
                    .build();
            Store store2 = Store.builder()
                    .email("smartit@gmail.com")
                    .password("strongPassword")
                    .description("Store for testing")
                    .name("Smart IT Clothing")
                    .address("California")
                    .gstinNumber("1234567890ABCDE")
                    .build();
            List<Store> stores = new ArrayList<>();
            stores.add(store2);
            stores.add(store);
            storeRepository.saveAll(stores);
        };
    }


    @Transactional
    public void disableStore(Long id) {
        Store store = this.getById(id);
        store.setLastModifiedOn(LocalDate.now());
        store.setActive(false);
    }

    public void deleteStore(Long id){
        storeRepository.deleteById(id);
    }

    @Transactional
    public Store updateStoreById(Long id, StoreDto store){
        Store store1 = this.getById(id);
        store1.setName(store.getName());
        store1.setDescription(store.getDescription());
        store1.setLastModifiedOn(LocalDate.now());
        return store1;
    }

    private void handleEmailAlreadyExistException(Store store) throws MethodArgumentNotValidException, NoSuchMethodException {
        BindingResult bindingResult = new BeanPropertyBindingResult(store, "store");
        bindingResult.rejectValue("email", "EmailInUse", "Email is already in use.");

        throw new MethodArgumentNotValidException(
                new MethodParameter(this.getClass().getDeclaredMethod("registerStore", Store.class), 0),
                bindingResult
        );
    }

    public Store getByEmail(String email) {
        return storeRepository.getStoreByEmail(email).orElseThrow(()->
                new StoreNotFoundException("Store with email: "+ email+ " does not exists!"));
    }
}
