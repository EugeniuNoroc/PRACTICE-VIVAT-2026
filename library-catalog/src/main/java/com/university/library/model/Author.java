package com.university.library.model;

import java.util.UUID;

public class Author {
    private UUID id;
    private String fullName;
    private int birthYear;
    private String biography;

    public Author(UUID id, String fullName, int birthYear, String biography) {
        this.id = id;
        this.fullName = fullName;
        this.birthYear = birthYear;
        this.biography = biography;
    }

    public UUID getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public String getBiography() {
        return biography;
    }
}
