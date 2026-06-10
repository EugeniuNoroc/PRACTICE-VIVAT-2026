package com.university.library.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ReaderUpdateRequest(
        @Schema(description = "Имя читателя", example = "Геннадий Федорович") String fullName,
        @Schema(description = "Электронная почта читателя", example = "example@example.com") String email
) {}
