package com.baseshelf.category;

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

    private String category_type;
}
