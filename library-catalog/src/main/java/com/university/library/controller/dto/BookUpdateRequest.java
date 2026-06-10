package com.university.library.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record BookUpdateRequest(
        @Schema(description = "Название книги", example = "Война и мир")  String title,
        @Schema(description = "Количество копий", example = "5") int copiesTotal) {
}
