package com.baseshelf.order;

import com.baseshelf.product.Product;
import com.baseshelf.utils.BaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
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
    @JsonBackReference
    private ProductOrder productOrder;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Positive(message = "Quantity should be greater than zero")
    private Integer quantity;

    @PositiveOrZero(message = "Amount must be equal to or greater than zero")
    private Float amount;

    @PositiveOrZero(message = "GST must be equal to or greater than zero")
    private Float gst;
}
