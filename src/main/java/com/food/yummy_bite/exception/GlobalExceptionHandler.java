package com.food.yummy_bite.exception;

import com.food.yummy_bite.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.net.http.WebSocket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex , WebRequest request){
        log.error("Resource Not Found: {}" , ex.getMessage());
        ErrorResponse error = ErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=",""))
                .build();
        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(BussinessException.class)
    public ResponseEntity<ErrorResponse> handleBussinessException(BussinessException ex , WebRequest request){
        log.error("Bussiness error: {} - {}" , ex.getErrorCode() , ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(ex.getMessage())
                .errorCode(ex.getErrorCode())
                .path(request.getDescription(false).replace("uri=",""))
                .build();

        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handlDuplicateResourceException(DuplicateResourceException ex , WebRequest request){
        log.error("Duplicate resource : {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error("Conlict")
                .message(ex.getMessage())
                .path(request.getDescription(false) .replace("uri=" , ""))
                .build();
        return ResponseEntity.status(error.getStatus()).body(error);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex , WebRequest request){
        Map<String , List<String>> vadilationErrors =new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error->{
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            vadilationErrors.computeIfAbsent(fieldName,k->new ArrayList<>()).add(errorMessage);
        });
        log.error("Validation failed : {}",vadilationErrors);

        ErrorResponse error = ErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Failed")
                .validationErrors(vadilationErrors)
                .path(request.getDescription(false).replace("uri=",""))
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex , WebRequest request){
        log.error("Unexpected error occured" , ex);

        ErrorResponse error =ErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("internal Server Error")
                .message("Unexpected internel server error , Please try again layer")
                .path(request.getDescription(false).replace("uri=",""))
                .build();
        return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
