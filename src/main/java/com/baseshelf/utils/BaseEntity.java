package com.baseshelf.utils;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private String name;

    @Column(updatable = false)
    private LocalDate createdOn;

    @Column(insertable = false)
    private LocalDate lastModifiedOn;

    @PrePersist
    public void setCreatedOn(){
        this.createdOn = LocalDate.now();
    }
}
