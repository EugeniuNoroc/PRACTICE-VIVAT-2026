package com.university.library.controller.dto;

import java.time.LocalDate;

public record ReaderCreateRequest(
        String fullName,
        String email,
        LocalDate registrationDate
) {}