package com.userregistration.exception;

import com.userregistration.error.ApiFieldError;
import com.userregistration.error.ErrorResponse;
import com.userregistration.service.RegistrationRuleException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<ApiFieldError> errors =
                ex.getBindingResult().getFieldErrors().stream()
                        .map(fe -> new ApiFieldError(fe.getField(), fe.getDefaultMessage()))
                        .toList();
        return ResponseEntity.badRequest().body(new ErrorResponse(errors));
    }

    @ExceptionHandler(RegistrationRuleException.class)
    public ResponseEntity<ErrorResponse> handleRegistrationRule(RegistrationRuleException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(List.of(new ApiFieldError(ex.getField(), ex.getMessage()))));
    }
}
