package com.baseshelf.product;

import com.baseshelf.brand.Brand;
import com.baseshelf.category.Category;
import com.baseshelf.order.ProductOrder;
import com.baseshelf.store.Store;
import com.baseshelf.utils.BaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Product extends BaseEntity {

    private String description;
    private Float sellingPrice;
    private Float costPrice;
    private boolean isSold;
    private LocalDate soldAt;
    private Integer quantity;
    private boolean isTaxed;
    private Float cgst;
    private Float sgst;

    @ManyToMany()
    @JoinTable(
            name = "product_categories",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"),
            indexes = {@Index(name = "categories", columnList = "category_id")}
    )
    private List<Category> categories = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "store_id")
    @JsonBackReference
    private Store store;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private ProductOrder productOrder;
}
