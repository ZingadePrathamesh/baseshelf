package com.baseshelf.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidGSTINException extends RuntimeException {
    public InvalidGSTINException(String s){
        super(s);
    }
}
