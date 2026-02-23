package com.ecopilot.project.exception;

import com.ecopilot.project.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleAllExceptions(Exception ex, WebRequest request) {
        return new ResponseEntity<>(ApiResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .error(ex.getClass().getSimpleName())
                .build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
