package com.university.library.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ReaderCreateRequest(
        @NotBlank @Size(min = 2, max = 100) @Schema(description = "Имя читателя", example = "Геннадий Федорович") String fullName,
        @NotBlank @Email @Schema(description = "Электронная почта читателя", example = "example@example.com") String email,
        @PastOrPresent @Schema(description = "Дата регистрации", example = "2025-06-07") LocalDate registrationDate
) {}