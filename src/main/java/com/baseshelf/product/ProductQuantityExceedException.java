package com.baseshelf.product;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ProductQuantityExceedException extends RuntimeException {
    public ProductQuantityExceedException(String message){
        super(message);
    }
}
