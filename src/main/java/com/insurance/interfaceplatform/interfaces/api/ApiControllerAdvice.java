package com.insurance.interfaceplatform.interfaces.api;

import com.insurance.interfaceplatform.support.error.CoreException;
import com.insurance.interfaceplatform.support.error.ErrorType;
import com.insurance.interfaceplatform.support.response.ApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiControllerAdvice {

    @ExceptionHandler(CoreException.class)
    public ResponseEntity<ApiResponse<Void>> handleCoreException(final CoreException exception) {
        final ErrorType errorType = exception.errorType();
        return ResponseEntity.status(errorType.status())
                .body(ApiResponse.fail(errorType.code(), exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(final MethodArgumentNotValidException exception) {
        final String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse(ErrorType.BAD_REQUEST.message());
        return ResponseEntity.status(ErrorType.BAD_REQUEST.status())
                .body(ApiResponse.fail(ErrorType.BAD_REQUEST.code(), message));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolationException(final DataIntegrityViolationException exception) {
        return ResponseEntity.status(ErrorType.CONFLICT.status())
                .body(ApiResponse.fail(ErrorType.CONFLICT.code(), ErrorType.CONFLICT.message()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(final Exception exception) {
        return ResponseEntity.status(ErrorType.INTERNAL_ERROR.status())
                .body(ApiResponse.fail(ErrorType.INTERNAL_ERROR.code(), ErrorType.INTERNAL_ERROR.message()));
    }
}
