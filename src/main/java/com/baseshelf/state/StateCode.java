package com.baseshelf.state;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StateCode {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "entity_seq")
    @SequenceGenerator(
            name = "entity_seq",
            sequenceName = "entity_seq",
            allocationSize = 100
    )
    private Long id;
    private String stateCode;
    private String stateName;
    private int tinNumber;
}
