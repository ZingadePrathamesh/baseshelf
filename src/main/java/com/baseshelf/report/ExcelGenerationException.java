package com.baseshelf.report;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
public class ExcelGenerationException extends RuntimeException {
    public ExcelGenerationException(String message) {
        super(message);
    }
}
