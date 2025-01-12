package com.baseshelf.product;

import com.baseshelf.brand.Brand;
import com.baseshelf.category.Category;
import com.baseshelf.store.Store;
import com.baseshelf.utils.BaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(
        indexes = {
                @Index(name = "store_id", columnList = "store_id"),
                @Index(name = "brand_id", columnList = "brand_id")
        }
)
public class Product extends BaseEntity {

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @PositiveOrZero(message = "Selling Price cannot be negative")
    private Float sellingPrice;

    @PositiveOrZero(message = "Discount rates cannot be negative")
    private Float discountRate;

    @PositiveOrZero(message = "Cost Price cannot be negative")
    private Float costPrice;

    private boolean taxed;

    @Size(max = 50, message = "hsnCode must not exceed 500 characters")
    private String hsnCode;

    @PositiveOrZero(message = "cgst cannot be negative")
    private Float cgst;

    @PositiveOrZero(message = "sgst cannot be negative")
    private Float sgst;

    @PositiveOrZero(message = "Product quantity should be greater than or equal to zero.")
    @NotNull(message = "Product quantity cannot be null value.")
    private Integer quantity;

    @Size(max = 50, message = "unitOfMeasure must not exceed 500 characters")
    private String unitOfMeasure;

    @ManyToMany()
    @JoinTable(
            name = "product_categories",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"),
            indexes = {
                    @Index(name = "idx_category_id", columnList = "category_id"),
                    @Index(name = "idx_product_id", columnList = "product_id"),
                    @Index(name = "idx_product_category", columnList = "product_id, category_id")
            }
    )
    private List<Category> categories = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "store_id")
    @JsonBackReference
    @NotNull(message = "Store cannot be null")
    private Store store;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "brand_id")
    private Brand brand;
}
