package com.university.library.controller.dto;

import java.util.UUID;

public record AuthorResponse(UUID id, String fullName, int birthYear, String biography) {
}
