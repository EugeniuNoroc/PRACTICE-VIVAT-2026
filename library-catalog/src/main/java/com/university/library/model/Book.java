package com.university.library.model;

public class Book {
    private Long id;
    private String title;
    private String isbn;
    private int year;
    private Long authorId;
    private int copiesTotal;
    private int copiesAvailable;

    public Book(Long id, String title, String isbn, int year, Long authorId, int copiesTotal, int copiesAvailable) {
        this.id = id;
        this.title = title;
        this.isbn = isbn;
        this.year = year;
        this.authorId = authorId;
        this.copiesTotal = copiesTotal;
        this.copiesAvailable = copiesAvailable;
    }

    public Long getId() {
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

    public Long getAuthorId() {
        return authorId;
    }

    public int getCopiesTotal() {
        return copiesTotal;
    }

    public int getCopiesAvailable() {
        return copiesAvailable;
    }
}
