package com.university.library.controller.dto;

import com.university.library.controller.ValidIsbn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record BookCreateRequest(
        @NotBlank @Schema(description = "Название книги", example = "Война и мир") String title,
        @ValidIsbn @Max(1000) @Schema(description = "ISBN книги", example = "978-5-04-116820-7") String isbn,
        @Min(1500) @Max(2100) @Schema(description = "Год издания", example = "1869") int year,
        @NotNull @Schema(description = "ID автора") UUID authorId,
        @Min(1) @Schema(description = "Количество копий", example = "5") int copiesTotal
) {}
