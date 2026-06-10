package com.university.library.controller.dto;

import com.university.library.controller.ValidIsbn;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record BookCreateRequest(
        @NotBlank String title,
        @ValidIsbn String isbn,
        @Min(1500) @Max(2100) int year,
        @NotNull UUID authorId,
        @Min(1) int copiesTotal) {
}
