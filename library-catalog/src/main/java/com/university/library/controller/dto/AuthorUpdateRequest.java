package com.university.library.controller.dto;

public record AuthorUpdateRequest(String fullName, int birthYear,String biography) {
}
