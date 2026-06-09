package com.university.library.controller.dto;

import java.util.UUID;

public record BookCreateRequest(String title, String isbn, int year, UUID authorId, int copiesTotal) {
}
