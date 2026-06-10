package com.university.library.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthorCreateRequest(
        @NotBlank @Size(min = 2, max = 100) @Schema(description = "Имя автора", example = "Лев Толстой") String fullName,
        @Max(2100) @Schema(description = "Год рождения", example = "2005") int birthYear,
        @Schema(description = "Биография автора", example = "С 12 лет написал множество произведений, что повлияло на целое литературное направление в жанре...") String biography) {
}
