package com.baseshelf.category;

import com.baseshelf.product.Product;
import jakarta.persistence.*;
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
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_seq")
    @SequenceGenerator(
            name = "category_seq",
            sequenceName = "category_seq",
            allocationSize = 100
    )
    private Long id;

    @Column(unique = true)
    private String name;

    private String categoryType;

    @ManyToMany(mappedBy = "categories")
    private List<Product> products = new ArrayList<>();
}
