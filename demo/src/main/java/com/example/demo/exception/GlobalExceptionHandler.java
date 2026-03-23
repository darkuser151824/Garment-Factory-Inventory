package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleStockException(
            InsufficientStockException ex) {

        ErrorResponse error =
                new ErrorResponse(false,ex.getMessage(),
                        HttpStatus.CONFLICT.value());

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidatioErrors(MethodArgumentNotValidException ex)
    {
        Map <String,String> errors=new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error->{errors.put(error.getField(),error.getDefaultMessage());});
        ErrorResponse errorResponse=new ErrorResponse(false,"Validation Failed",HttpStatus.BAD_REQUEST.value(),errors);

        return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException ex) {

        ErrorResponse error =
                new ErrorResponse(false,ex.getMessage(),
                        HttpStatus.CONFLICT.value());

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {

        ErrorResponse error =
                new ErrorResponse(false,ex.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value());

        return new ResponseEntity<>(error,
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
