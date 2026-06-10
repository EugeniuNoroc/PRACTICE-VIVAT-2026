package com.university.library.controller.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ApiError(
        LocalDateTime timestamp,
        int status,
        String error,
        String path,
        List<Violation> violations
) {
    public record Violation(String field, String message) {}
}
