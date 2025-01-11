package com.baseshelf.customer;

import com.baseshelf.order.ProductOrder;
import com.baseshelf.state.StateCode;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "entity_seq")
    @SequenceGenerator(
            name = "entity_seq",
            sequenceName = "entity_seq",
            allocationSize = 100
    )
    private Long id;
    private String name;
    private String gstin;
    private String address;
    private String contact;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "state_code_id")
    private StateCode stateCode;

    @OneToOne(mappedBy = "customer")
    @JsonBackReference
    private ProductOrder productOrder;
}
