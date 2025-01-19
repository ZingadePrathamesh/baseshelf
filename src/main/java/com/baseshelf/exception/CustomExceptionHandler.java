package com.baseshelf.exception;

import com.baseshelf.brand.BrandNotFoundException;
import com.baseshelf.category.CategoryAlreadyExist;
import com.baseshelf.category.CategoryNotFoundException;
import com.baseshelf.order.OrderNotFoundException;
import com.baseshelf.product.ProductNotFoundException;
import com.baseshelf.product.ProductQuantityExceedException;
import com.baseshelf.state.StateCodeNotFoundException;
import com.baseshelf.store.StoreNotFoundException;
import com.baseshelf.supplier.SupplierNotFoundException;
import com.baseshelf.utils.InvalidGSTINException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({
            CategoryNotFoundException.class, StoreNotFoundException.class,
            BrandNotFoundException.class, ProductNotFoundException.class, OrderNotFoundException.class,
            SupplierNotFoundException.class, StateCodeNotFoundException.class
    })
    public final ResponseEntity<ErrorDetails> handleResourceNotFoundException(
            Exception ex, WebRequest request)
    throws Exception{
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(), ex.getMessage(), request.getDescription(false)
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ProductQuantityExceedException.class, InvalidGSTINException.class})
    public final ResponseEntity<ErrorDetails> handleBadRequestException(
            Exception ex, WebRequest request)
    throws Exception{
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(), ex.getMessage(), request.getDescription(false)
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
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

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        Map<String, String> errorDetails = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(objectError -> {
            String field = ((FieldError) objectError).getField();
            String errorMessage = objectError.getDefaultMessage();
            errorDetails.put(field, errorMessage);
        });
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}
