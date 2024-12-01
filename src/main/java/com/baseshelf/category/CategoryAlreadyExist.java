package com.baseshelf.category;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//@Setter
//@Getter
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class CategoryAlreadyExist extends RuntimeException{
//    private Category category;

    public CategoryAlreadyExist(String message){
        super(message);
//        this.category = category;
    }
}
