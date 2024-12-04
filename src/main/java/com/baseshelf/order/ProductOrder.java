package com.baseshelf.order;

import com.baseshelf.product.Product;
import com.baseshelf.store.Store;
import com.baseshelf.utils.BaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
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
public class ProductOrder extends BaseEntity {
    private Integer productCount;
    private Double productTotalAmount;
    private Double gst;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "store_id")
    @JsonBackReference
    private Store store;

    @OneToMany(mappedBy = "productOrder", fetch = FetchType.EAGER)
    private List<Product> products;

    @PrePersist
    public void setProductCount(){
        this.productCount = products.size();
    }
}
