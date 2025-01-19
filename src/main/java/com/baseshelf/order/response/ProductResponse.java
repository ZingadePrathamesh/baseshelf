package com.baseshelf.order.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse{
    private Long id;
    private String name;
    private Float sellingPrice;
    private Float discountRate;
    private String unitOfMeasure;
    private String hsnCode;
}
