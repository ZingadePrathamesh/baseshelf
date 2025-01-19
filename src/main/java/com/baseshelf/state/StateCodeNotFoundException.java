package com.baseshelf.state;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class StateCodeNotFoundException extends RuntimeException {
    public StateCodeNotFoundException(String s) {
        super(s);
    }
}
