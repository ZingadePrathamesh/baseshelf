package com.baseshelf.order;

import com.baseshelf.customer.Customer;
import com.baseshelf.store.Store;
import com.baseshelf.utils.BaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
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

    @OneToMany(mappedBy = "productOrder", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<OrderItem> orderItems;

    @NotNull(message = "Item count cannot be null.")
    @PositiveOrZero(message = "Item count cannot be a negative number!")
    private int itemCount;

//    @PositiveOrZero(message = "Product Total Amount must be equal to or greater than zero")
    private Float totalAmountExcludingGst;

    private Float totalAmountIncludingGst;

//    @PositiveOrZero(message = "Total GST must be equal to or greater than zero")
    private Float totalGst;

    private String amountInWords;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "store_id")
    @JsonBackReference
    private Store store;
}

