package com.baseshelf.product;

import com.baseshelf.brand.Brand;
import com.baseshelf.category.Category;
import com.baseshelf.order.ProductOrder;
import com.baseshelf.store.Store;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Long id;

    @Size(message = "Name should be between 2 to 30 characters", max = 30, min = 2)
    @NotNull(message = "Name cannot be null.")
    private String name;

    @Column(updatable = false)
    private LocalDate createdOn;

    @Column(insertable = false)
    private LocalDate lastModifiedOn;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @PositiveOrZero(message = "Selling Price cannot be negative")
    private Float sellingPrice;

    @PositiveOrZero(message = "Cost Price cannot be negative")
    private Float costPrice;

    private boolean isSold;

    private LocalDate soldAt;

    private boolean isTaxed;

    @PositiveOrZero(message = "cgst cannot be negative")
    private Float cgst;

    @PositiveOrZero(message = "sgst cannot be negative")
    private Float sgst;

    private String barcode;

    private List<Category> categories = new ArrayList<>();

    @NotNull(message = "Store cannot be null")
    private Store store;

    private Brand brand;

    private ProductOrder productOrder;

    @Positive(message = "quantity cannot be zero or negative numbers.")
    private Integer quantity;
}
