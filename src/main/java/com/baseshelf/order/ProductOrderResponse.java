package com.baseshelf.order;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ProductOrderResponse {
    Long getId();
    LocalDate getCreatedOn();
    LocalTime getOrderTime();
    Float getTotalAmount();
    Float getTotalGst();
    List<OrderItemRes> getOrderItems();
    StoreRes getStore();

    interface OrderItemRes{
        Long getId();
        Integer getQuantity();
        Float getAmount();
        Float getGst();
        ProductRes getProduct();

        interface ProductRes{
            Long getId();
            String getName();
            Float getCgst();
            Float getSgst();
        }
    }

    interface StoreRes{
        String getName();
        String getDescription();
        String getGstinNumber();
    }
}


