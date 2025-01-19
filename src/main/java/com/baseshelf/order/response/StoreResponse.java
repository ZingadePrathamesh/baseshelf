package com.baseshelf.order.response;

import com.baseshelf.state.StateCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreResponse{
    private String name;
    private String description;
    private String gstinNumber;
    private String contactNumber;
    private String address;
    private StateCode stateCode;
}
