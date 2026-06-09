package com.university.library.controller.dto;

public record ReaderUpdateRequest(
        String fullName,
        String email
) {}