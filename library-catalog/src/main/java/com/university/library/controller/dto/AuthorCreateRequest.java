package com.university.library.controller.dto;

public record AuthorCreateRequest(String fullName, int birthYear, String biography) {
}
