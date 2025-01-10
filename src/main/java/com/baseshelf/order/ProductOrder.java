package com.baseshelf.order;

import com.baseshelf.store.Store;
import com.baseshelf.utils.BaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
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

    @OneToMany(mappedBy = "productOrder", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<OrderItem> orderItems;

    @NotNull(message = "Item count cannot be null.")
    @PositiveOrZero(message = "Item count cannot be a negative number!")
    private int itemCount;

//    @PositiveOrZero(message = "Product Total Amount must be equal to or greater than zero")
    private Float totalAmount;

//    @PositiveOrZero(message = "Total GST must be equal to or greater than zero")
    private Float totalGst;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "store_id")
    @JsonBackReference
    private Store store;
}

