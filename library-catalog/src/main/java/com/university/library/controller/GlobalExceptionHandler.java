package com.university.library.controller;

import com.university.library.controller.dto.ApiError;
import com.university.library.exception.EntityNotFoundException;
import com.university.library.exception.NoCopiesAvailableException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation (MethodArgumentNotValidException ex, HttpServletRequest request) {

        List<ApiError.Violation> violations = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> new ApiError.Violation(e.getField(), e.getDefaultMessage()))
                .toList();

        ApiError error = new ApiError(
                LocalDateTime.now(),
                400,
                "Validation Failed",
                request.getRequestURI(),
                violations
        );
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(Exception ex, HttpServletRequest request) {

        ApiError error = new ApiError(
                LocalDateTime.now(),
                404,
                ex.getMessage(),
                request.getRequestURI(),
                List.of()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(NoCopiesAvailableException.class)
    public ResponseEntity<ApiError> handleConflict(Exception ex, HttpServletRequest request) {

        ApiError error = new ApiError(
                LocalDateTime.now(),
                409,
                ex.getMessage(),
                request.getRequestURI(),
                List.of()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // TODO (W2 review): не хватает обработчика нарушения уникального ключа. Сейчас дубль ISBN (UNIQUE на books.isbn)
    //  -> DataIntegrityViolationException -> падает в handleAny -> 500, хотя Swagger на POST /api/books обещает
    //  409 "ISBN уже существует" (и это незакрытый критерий дня 2 — DuplicateKeyException).
    //  Добавь @ExceptionHandler(DataIntegrityViolationException.class) -> 409 CONFLICT.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAny(Exception ex, HttpServletRequest request) {

        ApiError error = new ApiError(
                LocalDateTime.now(),
                500,
                "Internal Server Error",
                request.getRequestURI(),
                List.of()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
