package com.baseshelf.store;

import com.baseshelf.category.CategoryService;
import com.baseshelf.state.StateCode;
import com.baseshelf.state.StateController;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/*
Overview
The StoreService class is a Spring Service that handles operations related to managing Store entities.
It interacts with the StoreRepository for database operations and the CategoryService for managing categories associated
with stores. The class includes methods for creating, retrieving, updating, and deleting stores, as well as handling
store-specific logic such as enabling/disabling and managing unique constraints.
*/

@Service
public class StoreService {

//    Handles persistence and query operations for Store entities.
    private final StoreRepository storeRepository;
//    Used to assign global categories to newly registered stores.
    private final CategoryService categoryService;

    /*
    * Injects dependencies into the class.
    * The @Lazy annotation is used for CategoryService to prevent circular dependency issues if they exist.
    */
    public StoreService(StoreRepository storeRepository, @Lazy CategoryService categoryService) {
        this.storeRepository = storeRepository;
        this.categoryService = categoryService;
    }

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
        Store savedStore = storeRepository.save(store);
        categoryService.saveGlobalCategoriesForStore(savedStore);
        return savedStore;
    }


    //Inserting test data for development environment. You can remove it if you want.
    @Bean
    @Order(value = 2)
    public CommandLineRunner insertStores(
            StoreRepository storeRepository, StateController controller
    ){
        return args -> {
            StateCode stateCode = controller.getAllStateCode().get(0);
            Store store = Store.builder()
                    .email("johndoe@gmail.com")
                    .password("strongPassword")
                    .description("Store for testing")
                    .name("John Doe Clothing")
                    .address("California")
                    .gstinNumber("1234567890ABCDE")
                    .contactNumber("9870654322")
                    .stateCode(stateCode)
                    .build();
            Store store2 = Store.builder()
                    .email("smartit@gmail.com")
                    .password("strongPassword")
                    .description("Store for testing")
                    .name("Smart IT Clothing")
                    .address("California")
                    .gstinNumber("1234567890ABCDE")
                    .contactNumber("9870654322")
                    .stateCode(stateCode)
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
