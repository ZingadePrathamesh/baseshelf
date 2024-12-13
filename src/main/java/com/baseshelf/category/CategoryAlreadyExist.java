package com.baseshelf.category;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class CategoryAlreadyExist extends RuntimeException{

    public CategoryAlreadyExist(String message){
        super(message);
    }
}
