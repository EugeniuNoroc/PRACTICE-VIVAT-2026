package com.university.library.model;

import java.time.LocalDate;

public class Reader {
    private Long id;
    private String fullName;
    private String email;
    private LocalDate registrationDate;

    public Reader(Long id, String fullName, String email, LocalDate registrationDate) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.registrationDate = registrationDate;
    }

    public Long getId() {
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
