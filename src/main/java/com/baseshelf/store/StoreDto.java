package com.baseshelf.store;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreDto {
    @Size(message = "Name should be between 2 to 30 characters", max = 30, min = 2)
    @NotNull(message = "Name cannot be null.")
    String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    String description;
}
