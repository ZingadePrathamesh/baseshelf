package com.baseshelf.store;

import com.baseshelf.brand.Brand;
import com.baseshelf.category.Category;
import com.baseshelf.order.ProductOrder;
import com.baseshelf.product.Product;
import com.baseshelf.utils.BaseEntity;
import com.baseshelf.state.StateCode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
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
public class Store extends BaseEntity {

    @Email(message = "Email should be valid")
    @NotNull(message = "Email is mandatory")
    private String email;

    @Size(min = 8, max = 30, message = "Password must be at least 8 characters long")
    @NotNull(message = "password cannot be null")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Size(max = 50, message = "Description must not exceed 60 characters")
    private String contactNumber;

    @Size(max = 50, message = "Description must not exceed 60 characters")
    private String address;

    @Size(min= 15, max = 16, message = "GSTIN must be 15 characters")
    private String gstinNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "state_code_id")
    private StateCode stateCode;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    @JsonManagedReference
    private List<Product> products;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    @JsonManagedReference
    private List<Brand> brands;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    @JsonManagedReference
    private List<Brand> supplier;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    @JsonManagedReference
    private List<ProductOrder> productOrders;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    @JsonIgnore
    private List<Category> categories;
}
