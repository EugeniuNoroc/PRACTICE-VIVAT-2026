package com.university.library.model;

public class Author {
    private Long id;
    private String fullName;
    private int birthYear;
    private String biography;

    public Author(Long id, String fullName, int birthYear, String biography) {
        this.id = id;
        this.fullName = fullName;
        this.birthYear = birthYear;
        this.biography = biography;
    }

    public Long getId() {
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
