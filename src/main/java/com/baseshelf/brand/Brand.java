package com.baseshelf.brand;

import com.baseshelf.product.Product;
import com.baseshelf.store.Store;
import com.baseshelf.utils.BaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Brand extends BaseEntity {
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "store_id")
    @JsonBackReference
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Store store;


    @OneToMany(mappedBy = "brand")
    @JsonBackReference
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<Product> products;
    
}
