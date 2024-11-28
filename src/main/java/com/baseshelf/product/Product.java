package com.baseshelf.product;

import com.baseshelf.category.Category;
import com.baseshelf.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {

    private String name;
    private String description;

    @ManyToMany()
    @JoinTable(
            name = "product_categories",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"),
            indexes = {@Index(name = "categories", columnList = "category_id")}
    )
    private List<Category> categories = new ArrayList<>();
}
