package com.baseshelf.category;

import com.baseshelf.product.Product;
import com.baseshelf.store.Store;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        indexes = {@Index(name = "category_type", columnList = "category_type")}
)
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_seq")
    @SequenceGenerator(
            name = "category_seq",
            sequenceName = "category_seq",
            allocationSize = 75
    )
    private Long id;

    @Size(message = "Name should be between 1 to 30 characters", max = 30, min = 1)
    @NotNull(message = "Name cannot be null.")
    private String name;

    @Size(message = "Category Type should be between 2 to 30 characters", max = 30, min = 2)
    @NotNull(message = "Category Type cannot be null.")
    private String categoryType;

    @ManyToMany(mappedBy = "categories")
    @JsonBackReference
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<Product> products = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "store_id")
    @JsonBackReference
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @NotNull(message = "Store cannot be null")
    private Store store;
}
