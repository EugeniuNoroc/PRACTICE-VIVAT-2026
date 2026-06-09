package com.university.library.controller;

import com.university.library.controller.dto.AuthorCreateRequest;
import com.university.library.controller.dto.AuthorResponse;
import com.university.library.controller.dto.AuthorUpdateRequest;
import com.university.library.model.Author;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuthorMapper {

    public AuthorResponse toResponse(Author author) {
        return new AuthorResponse(
                author.getId(),
                author.getFullName(),
                author.getBirthYear(),
                author.getBiography()
        );
    }

    public Author toEntity(AuthorCreateRequest request) {
        return new Author(
                UUID.randomUUID(),
                request.fullName(),
                request.birthYear(),
                request.biography()
        );
    }

    public Author applyUpdate(Author author, AuthorUpdateRequest request) {
        return new Author(
                author.getId(),
                request.fullName(),
                request.birthYear(),
                request.biography()
        );
    }
}
