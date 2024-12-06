package com.baseshelf.product;

import com.baseshelf.category.Category;
import com.baseshelf.category.CategoryService;
import com.baseshelf.store.Store;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductJpaRepository productJpaRepository;

    public List<Product> findProductWithoutCategory(String categoryType) {
        return productJpaRepository.findAllWithoutCategoryType(categoryType);
    }

    public List<Product> findAllProducts() {
        return productJpaRepository.findAll();
    }

    public List<Product> findAllByFilters(ProductFilter filter){
        List<Product> products = productJpaRepository.findAll(dynamicFilter(filter));
        return products;
    }

    public void deleteProductById(Long id){
        productJpaRepository.deleteById(id);
    }

    public Specification<Product> dynamicFilter(ProductFilter filter){
        return (root, query, criteriaBuilder) -> {
          List<Predicate> predicates = new ArrayList<>();

          if(filter.getLessThan() != null)
              predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("sellingPrice"), filter.getLessThan()));
          if(filter.getGreaterThan() != null)
              predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("sellingPrice"), filter.getGreaterThan()));


          return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    public Product getById(Long productId) {
        Optional<Product> product = productJpaRepository.findById(productId);
        return product.orElseThrow(()->
                new ProductNotFoundException("Product with id: " + productId + " does not exist."));
    }

    public Product getByIdAndStore(Long productId, Store store) {
        Optional<Product> productOptional = productJpaRepository.findByIdAndStore(productId, store);
        return productOptional.orElseThrow(()->
                new ProductNotFoundException("Product with id: " + productId + " does not exist."));
    }
}
