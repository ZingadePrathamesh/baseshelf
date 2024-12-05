package com.baseshelf.store;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.validation.AbstractBindingResult;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

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
            BindingResult bindingResult = new BeanPropertyBindingResult(store, "store");
            bindingResult.rejectValue("email", "EmailInUse", "Email is already in use.");

            throw new MethodArgumentNotValidException(
                    new MethodParameter(this.getClass().getDeclaredMethod("registerStore", Store.class), 0),
                    bindingResult
            );
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
                    .build();
            storeRepository.save(store);
        };
    }


}
