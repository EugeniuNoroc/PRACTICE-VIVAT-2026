package com.university.library.model;

import java.time.LocalDate;
import java.util.UUID;

public class Reader {
    private UUID id;
    private String fullName;
    private String email;
    private LocalDate registrationDate;

    public Reader(UUID id, String fullName, String email, LocalDate registrationDate) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.registrationDate = registrationDate;
    }

    public UUID getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }
}
