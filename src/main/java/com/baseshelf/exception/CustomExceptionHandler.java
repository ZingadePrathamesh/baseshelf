package com.baseshelf.exception;

import com.baseshelf.category.CategoryAlreadyExist;
import com.baseshelf.category.CategoryNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.LocalDateTime;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CategoryNotFoundException.class)
    public final ResponseEntity<ErrorDetails> handleResourceNotFoundException(
            Exception ex, WebRequest request)
    throws Exception{
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(), ex.getMessage(), request.getDescription(false)
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CategoryAlreadyExist.class)
    public final ResponseEntity<ErrorDetails> handleResourceAlreadyExists(
            Exception ex, WebRequest request
    ) throws Exception{
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(), ex.getMessage(), request.getDescription(false)
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}
