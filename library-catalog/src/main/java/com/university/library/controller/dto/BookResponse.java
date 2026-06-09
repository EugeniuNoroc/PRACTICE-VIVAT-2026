package com.university.library.controller.dto;

import java.util.UUID;

public record BookResponse(UUID id, String title, String isbn, int year, String authorName, int copiesAvailable) {
}
