package com.university.library.controller.dto;

import java.time.LocalDate;
import java.util.UUID;

public record ReaderResponse(
        UUID id,
        String fullName,
        String email,
        LocalDate registrationDate
) {}