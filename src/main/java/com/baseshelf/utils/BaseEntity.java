package com.baseshelf.utils;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@MappedSuperclass
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "entity_seq")
    @SequenceGenerator(
            name = "entity_seq",
            sequenceName = "entity_seq",
            allocationSize = 100
    )
    private Long id;

    @Column(updatable = false)
    private LocalDate createdOn;

    @Column(insertable = false)
    private LocalDate lastModifiedOn;
}
