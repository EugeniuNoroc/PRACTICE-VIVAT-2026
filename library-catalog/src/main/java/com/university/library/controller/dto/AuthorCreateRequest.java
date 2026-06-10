package com.university.library.controller.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthorCreateRequest(
        @NotBlank @Size(min = 2, max = 100) String fullName,
        @Max(2100) int birthYear,
        String biography) {
}
