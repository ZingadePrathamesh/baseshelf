package com.baseshelf.order;

import com.baseshelf.store.Store;
import com.baseshelf.utils.BaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ProductOrder extends BaseEntity {

    @NotNull(message = "Order Time cannot be null.")
    private LocalTime orderTime;

    @OneToMany(mappedBy = "productOrder", fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<OrderItem> orderItems;

    @PositiveOrZero(message = "Product Total Amount must be equal to or greater than zero")
    private Float productTotalAmount;

    @PositiveOrZero(message = "Total GST must be equal to or greater than zero")
    private Float totalGst;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "store_id")
    @JsonBackReference
    private Store store;
}
