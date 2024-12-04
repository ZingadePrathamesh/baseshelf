package com.baseshelf.store;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class StoreNotFoundException extends  RuntimeException{

    public StoreNotFoundException(String message){
        super(message);
    }
}
