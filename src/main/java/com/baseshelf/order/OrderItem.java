package com.baseshelf.order;

import com.baseshelf.product.Product;
import com.baseshelf.utils.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class OrderItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private ProductOrder productOrder;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Positive(message = "Quantity should be greater than zero")
    private Integer quantity;
}
