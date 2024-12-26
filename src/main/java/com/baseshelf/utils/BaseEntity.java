package com.baseshelf.utils;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@MappedSuperclass
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "entity_seq")
    @SequenceGenerator(
            name = "entity_seq",
            sequenceName = "entity_seq",
            allocationSize = 100
    )
    private Long id;

    @Size(message = "Name should be between 2 to 30 characters", max = 30, min = 2)
    @NotNull(message = "Name cannot be null.")
    private String name;

    @Column(updatable = false)
    private LocalDate createdOn;

    @Column(insertable = false)
    private LocalDate lastModifiedOn;

    private boolean isActive;

    @PrePersist
    public void setValuesPrePersist(){
        this.createdOn = LocalDate.now();
        this.isActive = true;
    }

    @PostPersist
    public void setValuesPostPersist(){
        this.lastModifiedOn = LocalDate.now();
    }
}
