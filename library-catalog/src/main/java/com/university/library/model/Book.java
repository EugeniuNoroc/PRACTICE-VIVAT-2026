package com.university.library.model;

import java.util.UUID;

public class Book {
    private UUID id;
    private String title;
    private String isbn;
    private int year;
    private UUID authorId;
    private int copiesTotal;
    private int copiesAvailable;

    public Book(UUID id, String title, String isbn, int year, UUID authorId, int copiesTotal, int copiesAvailable) {
        this.id = id;
        this.title = title;
        this.isbn = isbn;
        this.year = year;
        this.authorId = authorId;
        this.copiesTotal = copiesTotal;
        this.copiesAvailable = copiesAvailable;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getIsbn() {
        return isbn;
    }

    public int getYear() {
        return year;
    }

    public UUID getAuthorId() {
        return authorId;
    }

    public int getCopiesTotal() {
        return copiesTotal;
    }

    public int getCopiesAvailable() {
        return copiesAvailable;
    }
}
