package com.university.library.model;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.UUID;

public class BookWithAuthor {
    private UUID id;
    private String title;
    private String isbn;
    private String authorName;

    public BookWithAuthor(UUID id, String title, String isbn, String authorName) {
        this.id = id;
        this.title = title;
        this.isbn = isbn;
        this.authorName = authorName;
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

    public String getAuthorName() {
        return authorName;
    }
}
